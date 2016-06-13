package com.derby.nuke.wheatgrass.repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.derby.nuke.wheatgrass.entity.User;

class UserSpecifications {
	public static Specification<User> birthdayPattern(String pattern) {
		return new Specification<User>() {
			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate p = cb.like(root.get("birthday").as(String.class), pattern);
				return p;
			}
		};

	}
}
