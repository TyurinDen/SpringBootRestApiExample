package com.websystique.springboot.service.vkInfoBotClasses.entities;

public class Student {
    private long id;
    private String firstName;
    private String lastName;
    private int age;
    private String email;
    private String topic;

    public Student(Client client, String topic) {
        this.id = client.getId();
        this.firstName = client.getFirstName();
        this.lastName = client.getLastName();
        this.age = client.getAge();
        this.email = client.getEmail();
        this.topic = topic;
    }

    public Student(long id, String firstName, String lastName, int age, String email, String topic) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.email = email;
        this.topic = topic;
    }

    @Override
    public String toString() {
        return "Student {" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                ", topic='" + topic + '\'' +
                '}';
    }
}
