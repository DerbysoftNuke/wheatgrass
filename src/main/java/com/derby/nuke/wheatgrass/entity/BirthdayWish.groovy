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
@EqualsAndHashCode(excludes = [])
@ToString(excludes = [])
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
@Table(uniqueConstraints = [@UniqueConstraint(columnNames = ["user_id", "birthday"])])
class BirthdayWish {

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

    @Type(type = "dataMapType")
    @Column
    Map<String, Integer> giftCounts = new HashMap<>();

    Integer getTotalGiftCount() {
        def totalCount = 0;
        giftCounts.each {type,count->
            totalCount += count
        }
        return totalCount
    }
}