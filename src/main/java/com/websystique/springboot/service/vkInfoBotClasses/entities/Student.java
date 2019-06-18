package com.websystique.springboot.service.vkInfoBotClasses.entities;

public class Student extends Client {
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
        super(id, firstName, lastName, age, email);
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
