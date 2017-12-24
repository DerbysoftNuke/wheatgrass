package com.derby.nuke.wheatgrass.entity

import com.derby.nuke.wheatgrass.hibernate.DataMapUserType
import com.derby.nuke.wheatgrass.hibernate.SimpleCollectionType
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.google.common.collect.Sets
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Parameter
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.TypeDefs

import javax.persistence.*
import java.time.LocalDate

@Entity
@TypeDefs([
	@TypeDef(name = "setType", typeClass = SimpleCollectionType.class, parameters = [
		@Parameter(name = SimpleCollectionType.SPLIT, value = ","),
		@Parameter(name = SimpleCollectionType.ITEM_TYPE, value = "java.lang.String"),
		@Parameter(name = SimpleCollectionType.COLLECTION_TYPE, value = "java.util.HashSet")
	]),
	@TypeDef(name = "dataMapType", typeClass = DataMapUserType.class, parameters = [
			@Parameter(name = DataMapUserType.KEY_TYPE, value = "java.lang.String"),
			@Parameter(name = DataMapUserType.VALUE_TYPE, value = "java.lang.Integer")
	])
])
@EqualsAndHashCode(excludes=[])
@ToString(excludes=[])
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
@Table(uniqueConstraints=[@UniqueConstraint(columnNames=["user_id", "birthday"])])
class BirthdayWish{

	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	@Column(columnDefinition = "CHAR(32)")
	@Id
	String id;
	
	Date createTime;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	User user;

	// birthday of this year
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	LocalDate birthday;
	
	@OneToMany(mappedBy = "birthdayWish", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval=true)
	List<BirthdayWishWord> birthdayWishWords=new ArrayList<>();

	@Type(type = "dataMapType")
	@Column
	Map<String, Integer> giftCounts = new HashMap<>();

	@Type(type = "setType")
	@Column(length = 16777215)
	Set<String> sendFlowerUserIds = new HashSet();
	
	@Type(type = "setType")
	@Column(length = 16777215)
	Set<String> sendCakeUserIds = new HashSet();
	
	@Type(type = "setType")
	@Column(length = 16777215)
	Set<String> sendFireworkUserIds = new HashSet();
	
	Set<String> getSendWishWordUserIds (){
		Set<String> userIds = Sets.newHashSet();
		for(birthdayWishWord in birthdayWishWords){
			userIds.add(birthdayWishWord.getWisher().getUserId());
		}
		return userIds;
	}

	def countGifts(Collection<String> gifts){
		for (String  gift: gifts) {
			countGift(gift)
		}
	}
    def countGift(String gift){
        Integer count = giftCounts.getOrDefault(gift, 0) + 1
        giftCounts.put(gift, count)
        return count
    }

}