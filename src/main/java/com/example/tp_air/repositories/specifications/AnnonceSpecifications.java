package com.example.tp_air.repositories.specifications;

import com.example.tp_air.models.Annonce;
import com.example.tp_air.models.AnnonceStatus;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AnnonceSpecifications {

    public static Specification<Annonce> withFilters(String q, String status, Long categoryId, Long authorId,
                                                      LocalDateTime fromDate, LocalDateTime toDate) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(q)) {
                String likePattern = "%" + q.toLowerCase() + "%";
                Predicate titlePredicate = cb.like(cb.lower(root.get("title")), likePattern);
                Predicate descPredicate = cb.like(cb.lower(root.get("description")), likePattern);
                predicates.add(cb.or(titlePredicate, descPredicate));
            }

            if (StringUtils.hasText(status)) {
                try {
                    AnnonceStatus enumStatus = AnnonceStatus.valueOf(status.toUpperCase());
                    predicates.add(cb.equal(root.get("status"), enumStatus));
                } catch (IllegalArgumentException ignored){}
            }

            if (authorId != null)
                predicates.add(cb.equal(root.get("author").get("id"), authorId));

            if (fromDate != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), fromDate));

            if (toDate != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), toDate));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
