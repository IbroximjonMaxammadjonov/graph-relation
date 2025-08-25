package com.ibroximjon.graphrelation.service;

import com.ibroximjon.graphrelation.dto.GraphResponse;
import com.ibroximjon.graphrelation.dto.GraphResponse.Edge;
import com.ibroximjon.graphrelation.dto.GraphResponse.Node;
import com.ibroximjon.graphrelation.entity.Company;
import com.ibroximjon.graphrelation.entity.CompanyPerson;
import com.ibroximjon.graphrelation.entity.CompanyRelationship;
import com.ibroximjon.graphrelation.entity.Person;
import com.ibroximjon.graphrelation.repository.CompanyPersonRepository;
import com.ibroximjon.graphrelation.repository.CompanyRelationshipRepository;
import com.ibroximjon.graphrelation.repository.CompanyRepository;
import com.ibroximjon.graphrelation.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GraphService {

    private final CompanyRepository companyRepo;
    private final PersonRepository personRepo;
    private final CompanyPersonRepository cpRepo;
    private final CompanyRelationshipRepository crRepo;  // 🔥 yangi qo‘shildi

    public GraphResponse buildFullGraph(String id) {
        Optional<Company> startCompanyOpt = companyRepo.findByInn(id);
        Optional<Person>  startPersonOpt  = personRepo.findByPinfl(id);

        if (startCompanyOpt.isEmpty() && startPersonOpt.isEmpty()) {
            throw new RuntimeException("Hech narsa topilmadi: INN/PINFL = " + id);
        }

        GraphResponse resp = new GraphResponse();

        Set<Long> visitedCompanies = new HashSet<>();
        Set<Long> visitedPersons   = new HashSet<>();

        Deque<Company> qCompany = new ArrayDeque<>();
        Deque<Person>  qPerson  = new ArrayDeque<>();

        if (startCompanyOpt.isPresent()) {
            Company c = startCompanyOpt.get();
            addCompanyNodeOnce(resp, c);
            visitedCompanies.add(c.getId());
            qCompany.add(c);
        }
        if (startPersonOpt.isPresent()) {
            Person p = startPersonOpt.get();
            addPersonNodeOnce(resp, p);
            visitedPersons.add(p.getId());
            qPerson.add(p);
        }

        while (!qCompany.isEmpty() || !qPerson.isEmpty()) {

            // --- 1) kompaniya → person
            int cs = qCompany.size();
            for (int i = 0; i < cs; i++) {
                Company company = qCompany.pollFirst();
                if (company == null) continue;

                List<CompanyPerson> cps = cpRepo.findByCompany(company);
                for (CompanyPerson cp : cps) {
                    Person p = cp.getPerson();

                    addPersonNodeOnce(resp, p);
                    addEdgeOnce(resp, "person-" + p.getId(),
                            "company-" + company.getId(),
                            cp.getRole().name());

                    if (visitedPersons.add(p.getId())) {
                        qPerson.addLast(p);
                    }
                }

                // --- 2) kompaniya → boshqa kompaniya (Founder)
                List<CompanyRelationship> rels = crRepo.findByParentCompanyId(company.getId());
                for (CompanyRelationship rel : rels) {
                    Company child = rel.getChildCompany();

                    addCompanyNodeOnce(resp, child);
                    addEdgeOnce(resp, "company-" + company.getId(),
                            "company-" + child.getId(),
                            "FOUNDER");

                    if (visitedCompanies.add(child.getId())) {
                        qCompany.addLast(child);
                    }
                }

                // --- 3) kompaniya ← boshqa kompaniya (kim asos solgan)
                List<CompanyRelationship> rels2 = crRepo.findByChildCompanyId(company.getId());
                for (CompanyRelationship rel : rels2) {
                    Company parent = rel.getParentCompany();

                    addCompanyNodeOnce(resp, parent);
                    addEdgeOnce(resp, "company-" + parent.getId(),
                            "company-" + company.getId(),
                            "FOUNDER");

                    if (visitedCompanies.add(parent.getId())) {
                        qCompany.addLast(parent);
                    }
                }
            }

            // --- 4) shaxs → kompaniya
            int ps = qPerson.size();
            for (int i = 0; i < ps; i++) {
                Person person = qPerson.pollFirst();
                if (person == null) continue;

                List<CompanyPerson> cps = cpRepo.findByPerson(person);
                for (CompanyPerson cp : cps) {
                    Company c = cp.getCompany();

                    addCompanyNodeOnce(resp, c);
                    addEdgeOnce(resp, "person-" + person.getId(),
                            "company-" + c.getId(),
                            cp.getRole().name());

                    if (visitedCompanies.add(c.getId())) {
                        qCompany.addLast(c);
                    }
                }
            }
        }

        return resp;
    }

    private void addCompanyNodeOnce(GraphResponse resp, Company c) {
        String id = "company-" + c.getId();
        boolean exists = resp.getNodes().stream().anyMatch(n -> n.getId().equals(id));
        if (!exists) resp.getNodes().add(new Node(id, c.getName(), "COMPANY"));
    }

    private void addPersonNodeOnce(GraphResponse resp, Person p) {
        String id = "person-" + p.getId();
        boolean exists = resp.getNodes().stream().anyMatch(n -> n.getId().equals(id));
        if (!exists) resp.getNodes().add(new Node(id, p.getFullName(), "PERSON"));
    }

    private void addEdgeOnce(GraphResponse resp, String from, String to, String label) {
        boolean exists = resp.getEdges().stream()
                .anyMatch(e -> e.getFrom().equals(from) && e.getTo().equals(to) && Objects.equals(e.getLabel(), label));
        if (!exists) resp.getEdges().add(new Edge(from, to, label));
    }
}
