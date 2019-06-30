package com.websystique.springboot.service.vkInfoBotClasses.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

@Entity
@Table(name = "test_clients")
@NamedQueries({
        @NamedQuery(name = "getAll", query = "SELECT tc FROM TestClient tc"),
        @NamedQuery(name = "getByFirstName", query = "SELECT tc FROM TestClient tc WHERE tc.firstName = :firstName")})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TestClient implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private long id;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "city")
    private String city;

    @Column(name = "country")
    private String country;

    @Column(name = "client_description_comment")
    private String clientDescriptionComment;

    @Column(name = "comment")
    private String comment;

    @Column(name = "client_state")
    private String clientState;

    @Column(name = "postpone_comment")
    private String postponeComment;

    @Column(name = "birth_date")
    private Date birthDate;

    @Column(name = "manager_lastName")
    private String managerLastName;

    @Column(name = "manager_firstName")
    private String managerFirstName;

    @Column(name = "mentor_lastName")
    private String mentorLastName;

    @Column(name = "mentor_firstName")
    private String mentorFirstName;

}

