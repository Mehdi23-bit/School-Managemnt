package org.school.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity //hna kan golo lhibernate anaho Student how rah tableau f base donnes so rd lih lbal 
public class Student{
@Id
private int id;
private String name;
public Student(){}
public Student(int id,String name){
    this.id=id;
    this.name=name;

}
//getters and setters 
int getId(){
    return this.id;
}
String getName(){
    return this.name;
}
void setId(int id){
    this.id=id;
}
void setName(String name){
    this.name=name;
}


}