package com.derby.nuke.wheatgrass.repository;

import java.time.LocalDate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.derby.nuke.wheatgrass.entity.User;

class UserSpecifications {
	public static Specification<User> userHasBirthday(LocalDate today) {
		return new Specification<User>() {
			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				String todayStr = today.toString().split("-", 2)[1];
				Predicate p1 = cb.like(root.get("birthday").as(String.class), "%" + todayStr);
				if (!(!today.isLeapYear() && today.getMonthValue() == 2 && today.getDayOfMonth() == 28)) {
					return p1;
				}
				Predicate p2 = cb.like(root.get("birthday").as(String.class), "%02-29");
				return cb.or(p1, p2);
			}
		};

	}
}
