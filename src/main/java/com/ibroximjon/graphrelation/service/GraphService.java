package com.ibroximjon.graphrelation.service;

import com.ibroximjon.graphrelation.dto.GraphResponse;
import com.ibroximjon.graphrelation.dto.GraphResponse.Edge;
import com.ibroximjon.graphrelation.dto.GraphResponse.Node;
import com.ibroximjon.graphrelation.entity.Company;
import com.ibroximjon.graphrelation.entity.CompanyPerson;
import com.ibroximjon.graphrelation.entity.Person;
import com.ibroximjon.graphrelation.repository.CompanyPersonRepository;
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

    /**
     * Id — INN (company) yoki PINFL (person) bo‘lishi mumkin.
     * Topilgan tugundan boshlab to‘liq grafni BFS bilan quradi.
     */
    public GraphResponse buildFullGraph(String id) {
        Optional<Company> startCompanyOpt = companyRepo.findByInn(id);
        Optional<Person>  startPersonOpt  = personRepo.findByPinfl(id);

        if (startCompanyOpt.isEmpty() && startPersonOpt.isEmpty()) {
            throw new RuntimeException("Hech narsa topilmadi: INN/PINFL = " + id);
        }

        GraphResponse resp = new GraphResponse();

        // ko‘rilgan tugunlar
        Set<Long> visitedCompanies = new HashSet<>();
        Set<Long> visitedPersons   = new HashSet<>();

        // BFS navbatlari
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

        // BFS: company <-> person qatlamlari almashinadi
        while (!qCompany.isEmpty() || !qPerson.isEmpty()) {

            // 1) kompaniyalardan shaxslarga
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
            }

            // 2) shaxslardan kompaniyalarga
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
