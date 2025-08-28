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
    private final CompanyRelationshipRepository crRepo;

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

        // 🔹 Bu setsni har so‘rovda yaratamiz
        Set<String> existingNodes = new HashSet<>();
        Set<String> existingEdges = new HashSet<>();

        if (startCompanyOpt.isPresent()) {
            Company c = startCompanyOpt.get();
            addCompanyNodeOnce(resp, c, existingNodes);
            visitedCompanies.add(c.getId());
            qCompany.add(c);
        }
        if (startPersonOpt.isPresent()) {
            Person p = startPersonOpt.get();
            addPersonNodeOnce(resp, p, existingNodes);
            visitedPersons.add(p.getId());
            qPerson.add(p);
        }

        while (!qCompany.isEmpty() || !qPerson.isEmpty()) {

            int cs = qCompany.size();
            for (int i = 0; i < cs; i++) {
                Company company = qCompany.pollFirst();
                if (company == null) continue;

                List<CompanyPerson> cps = cpRepo.findByCompany(company);
                for (CompanyPerson cp : cps) {
                    Person p = cp.getPerson();
                    addPersonNodeOnce(resp, p, existingNodes);
                    addEdgeOnce(resp, "person-" + p.getId(), "company-" + company.getId(),
                            cp.getRole().name(), existingEdges);

                    if (visitedPersons.add(p.getId())) qPerson.addLast(p);
                }

                List<CompanyRelationship> rels = crRepo.findByParentCompanyId(company.getId());
                for (CompanyRelationship rel : rels) {
                    Company child = rel.getChildCompany();
                    addCompanyNodeOnce(resp, child, existingNodes);
                    addEdgeOnce(resp, "company-" + company.getId(), "company-" + child.getId(),
                            "FOUNDER", existingEdges);

                    if (visitedCompanies.add(child.getId())) qCompany.addLast(child);
                }

                List<CompanyRelationship> rels2 = crRepo.findByChildCompanyId(company.getId());
                for (CompanyRelationship rel : rels2) {
                    Company parent = rel.getParentCompany();
                    addCompanyNodeOnce(resp, parent, existingNodes);
                    addEdgeOnce(resp, "company-" + parent.getId(), "company-" + company.getId(),
                            "FOUNDER", existingEdges);

                    if (visitedCompanies.add(parent.getId())) qCompany.addLast(parent);
                }
            }

            int ps = qPerson.size();
            for (int i = 0; i < ps; i++) {
                Person person = qPerson.pollFirst();
                if (person == null) continue;

                List<CompanyPerson> cps = cpRepo.findByPerson(person);
                for (CompanyPerson cp : cps) {
                    Company c = cp.getCompany();
                    addCompanyNodeOnce(resp, c, existingNodes);
                    addEdgeOnce(resp, "person-" + person.getId(), "company-" + c.getId(),
                            cp.getRole().name(), existingEdges);

                    if (visitedCompanies.add(c.getId())) qCompany.addLast(c);
                }
            }
        }

        return resp;
    }

    private void addCompanyNodeOnce(GraphResponse resp, Company c, Set<String> existingNodes) {
        String id = "company-" + c.getId();
        if (existingNodes.add(id)) {
            resp.getNodes().add(new Node(id, c.getName(), "COMPANY"));
        }
    }

    private void addEdgeOnce(GraphResponse resp, String from, String to, String label, Set<String> existingEdges) {
        String key = from + "->" + to + ":" + label;
        if (existingEdges.add(key)) {
            resp.getEdges().add(new Edge(from, to, label));
        }
    }

    private void addPersonNodeOnce(GraphResponse resp, Person p, Set<String> existingNodes) {
        String id = "person-" + p.getId();
        if (existingNodes.add(id)) {
            resp.getNodes().add(new Node(id, p.getFullName(), "PERSON"));
        }
    }
}




