//package com.ibroximjon.graphrelation.service;
//
//import com.ibroximjon.graphrelation.dto.GraphResponse;
//import com.ibroximjon.graphrelation.dto.GraphResponse.Edge;
//import com.ibroximjon.graphrelation.dto.GraphResponse.Node;
//import com.ibroximjon.graphrelation.entity.Company;
//import com.ibroximjon.graphrelation.entity.CompanyPerson;
//import com.ibroximjon.graphrelation.entity.Person;
//import com.ibroximjon.graphrelation.repository.CompanyPersonRepository;
//import com.ibroximjon.graphrelation.repository.CompanyRepository;
//import com.ibroximjon.graphrelation.repository.PersonRepository;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class GraphServiceOld {
//
//    private final CompanyRepository companyRepo;
//    private final PersonRepository personRepo;
//    private final CompanyPersonRepository cpRepo;
//
//    public GraphServiceOld(CompanyRepository companyRepo, PersonRepository personRepo, CompanyPersonRepository cpRepo) {
//        this.companyRepo = companyRepo;
//        this.personRepo = personRepo;
//        this.cpRepo = cpRepo;
//    }
//
//    /** Yagona kirish nuqtasi: INN yoki PINFL (yoki ikkalasi null bo‘lmasin). */
//    public GraphResponse buildGraph(String inn, String pinfl) {
//        GraphResponse resp = new GraphResponse();
//
//        if (inn != null && !inn.isBlank()) {
//            Company company = companyRepo.findByInn(inn)
//                    .orElseThrow(() -> new RuntimeException("Company not found"));
//
//            resp.getNodes().add(new Node("company-" + company.getId(), company.getName(), "COMPANY"));
//
//            // 1-daraja: kompaniyaga aloqador shaxslar
//            List<CompanyPerson> relations = cpRepo.findByCompany(company);
//            for (CompanyPerson cp : relations) {
//                Person p = cp.getPerson();
//
//                resp.getNodes().add(new Node("person-" + p.getId(), p.getFullName(), "PERSON"));
//                resp.getEdges().add(new Edge("person-" + p.getId(), "company-" + company.getId(), cp.getRole().name()));
//
//                // 2-daraja: shu shaxs boshqa kompaniyalarda ham bo‘lsa
//                List<CompanyPerson> otherRelations = cpRepo.findByPerson(p);
//                for (CompanyPerson other : otherRelations) {
//                    Company otherCompany = other.getCompany();
//                    if (!otherCompany.getId().equals(company.getId())) {
//                        resp.getNodes().add(new Node("company-" + otherCompany.getId(), otherCompany.getName(), "COMPANY"));
//                        resp.getEdges().add(new Edge("person-" + p.getId(), "company-" + otherCompany.getId(), other.getRole().name()));
//                    }
//                }
//            }
//        }
//
//        if (pinfl != null && !pinfl.isBlank()) {
//            Person person = personRepo.findByPinfl(pinfl)
//                    .orElseThrow(() -> new RuntimeException("Person not found"));
//
//            resp.getNodes().add(new Node("person-" + person.getId(), person.getFullName(), "PERSON"));
//
//            // 1-daraja: shaxs aloqador kompaniyalar
//            List<CompanyPerson> relations = cpRepo.findByPerson(person);
//            for (CompanyPerson cp : relations) {
//                Company company = cp.getCompany();
//
//                resp.getNodes().add(new Node("company-" + company.getId(), company.getName(), "COMPANY"));
//                resp.getEdges().add(new Edge("person-" + person.getId(), "company-" + company.getId(), cp.getRole().name()));
//
//                // 2-daraja: shu kompaniyaning boshqa shaxslari
//                List<CompanyPerson> otherRelations = cpRepo.findByCompany(company);
//                for (CompanyPerson other : otherRelations) {
//                    Person otherPerson = other.getPerson();
//                    if (!otherPerson.getId().equals(person.getId())) {
//                        resp.getNodes().add(new Node("person-" + otherPerson.getId(), otherPerson.getFullName(), "PERSON"));
//                        resp.getEdges().add(new Edge("person-" + otherPerson.getId(), "company-" + company.getId(), other.getRole().name()));
//                    }
//                }
//            }
//        }
//
//        return resp;
//    }
//
//    // 🔁 Orqaga moslik: eski nomdagi metodlarni qaytarib qo‘ydik
//    public GraphResponse buildGraphFromCompanyInn(String inn) {
//        return buildGraph(inn, null);
//    }
//
//    public GraphResponse buildGraphFromPersonPinfl(String pinfl) {
//        return buildGraph(null, pinfl);
//    }
//}
