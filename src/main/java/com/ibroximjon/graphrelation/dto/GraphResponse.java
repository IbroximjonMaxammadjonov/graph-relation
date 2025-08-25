package com.ibroximjon.graphrelation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GraphResponse {
    private Set<Node> nodes = new HashSet<>();
    private Set<Edge> edges = new HashSet<>();

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class Node {
        private String id;     // "company-1" yoki "person-5"
        private String label;  // ko‘rinadigan nom
        private String type;   // "COMPANY" / "PERSON"

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Node n)) return false;
            return Objects.equals(id, n.id);
        }
        @Override public int hashCode() { return Objects.hash(id); }
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class Edge {
        private String from;    // manba nodeId
        private String to;      // manzil nodeId
        private String label;   // DIRECTOR/FOUNDER

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Edge e)) return false;
            return Objects.equals(from, e.from) &&
                    Objects.equals(to, e.to) &&
                    Objects.equals(label, e.label);
        }
        @Override public int hashCode() { return Objects.hash(from, to, label); }
    }
}
