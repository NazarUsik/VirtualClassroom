package com.infostroy.usik.modal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

@Entity(name = "Student")
public class Student implements com.infostroy.usik.modal.entity.Entity {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "hand")
    private String hand;

    public enum HandStatus {
        UP, DOWN;

        public static HandStatus getHandStatus(int id) {
            return HandStatus.values()[id];
        }

        public static int getHandStatusId(String status) {
            HandStatus[] types = HandStatus.values();
            for (int i = 0; i < types.length; i++) {
                if (types[i].getName().equals(status)) {
                    return i;
                }
            }
            return -1;
        }

        public String getName() {
            return name().toUpperCase();
        }
    }

    public Student() {
    }

    public Student(String name) {
        this.name = name;
        this.hand = HandStatus.DOWN.getName();
    }

    public Student(String name, HandStatus hand) {
        this.name = name;
        this.hand = hand.getName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHand() {
        return hand;
    }

    public void setHand(HandStatus hand) {
        this.hand = hand.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Student student = (Student) o;

        return Objects.equals(id, student.id) &&
                Objects.equals(name, student.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", hand=" + hand +
                '}';
    }
}
