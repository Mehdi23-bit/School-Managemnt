// src/test/java/org/school/entities/AllEntitiesTest.java
package org.school.entities;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;
import java.time.LocalDate;
import java.util.List;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AllEntitiesTest {

    private static EntityManagerFactory emf;
    private EntityManager em;

    @BeforeAll
static void init() {
    emf = Persistence.createEntityManagerFactory("testPU", Map.of(
        "jakarta.persistence.jdbc.driver", "com.mysql.cj.jdbc.Driver",
        "jakarta.persistence.jdbc.url", "jdbc:mysql://localhost:3306/school_test_db?createDatabaseIfNotExist=true",
        "jakarta.persistence.jdbc.user", "root",
        "jakarta.persistence.jdbc.password", "mahdi2005",
        "hibernate.dialect", "org.hibernate.dialect.MySQLDialect",
        "hibernate.hbm2ddl.auto", "create-drop",
        "hibernate.show_sql", "true",
        "hibernate.physical_naming_strategy", "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy"
    ));
}

    @BeforeEach
    void setUp() {
        em = emf.createEntityManager();
        em.getTransaction().begin();
    }

    @AfterEach
    void tearDown() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        em.close();
    }

    @AfterAll
    static void destroy() {
        if (emf != null) emf.close();
    }

    @Test
    @Order(1)
    @DisplayName("Test Major → Classe relationship")
    void testMajorWithClasses() {
        Major info = new Major("INFO", "Informatique", 2);
        em.persist(info);
        Classe c1 = new Classe("1ère Année Info A","2 me",40);
        Classe c2 = new Classe("2ème Année Info","djd",89);

        info.addClass(c1);
        info.addClass(c2);
      c1.setMajor(info);
      c2.setMajor(info);
       
        em.getTransaction().commit();
        em.clear();

        Major found = em.find(Major.class, info.getId());
        assertEquals(2, found.getClasses().size());
        assertEquals("Informatique", found.getName());
    }

    @Test
    @Order(2)
    @DisplayName("Test Student belongs to one Classe")
    void testStudentInClass() {
        Major info = new Major("INFO", "Informatique", 2);
        em.persist(info);
        Classe classe = new Classe("Terminale S","dj",38);
        classe.setMajor(info);
        Student s = new Student();
        s.setCne("GA987654");
        s.setUsername("mohamed2024");
        s.setPassword("pass");
        s.setFirstName("Mohamed");
        s.setLastName("Alaoui");
        s.setDateOfBirth(LocalDate.of(2006, 5, 15));
        s.setClasse(classe);

        classe.getStudents().add(s);

        em.persist(classe);
        em.getTransaction().commit();
        em.clear();

        Student found = em.createQuery("SELECT s FROM Student s WHERE s.cne = :cne", Student.class)
                .setParameter("cne", "GA987654")
                .getSingleResult();
        assertEquals("Terminale S", found.getClasse().getName());
    }

    @Test
    @Order(3)
    @DisplayName("Test Professor teaches ONE subject but in MANY classes")
    void testProfessorTeachesMultipleClasses() {
        Subject math = new Subject( "Mathématiques", 6);
        Major info = new Major("INFO", "Informatique", 2);
        em.persist(info);
        Classe c1 = new Classe("1ère Info A","jd",38);
        Classe c2 = new Classe("1ère Info B","djd",38);
        Classe c3 = new Classe("2ème Info","djd",37);
        c1.setMajor(info);
        c2.setMajor(info);
        c3.setMajor(info);


        Professor prof = new Professor("prof.ahmed", "123456", "Ahmed Benali", "ahmed@school.ma", math);
        prof.addClass(c1);
        prof.addClass(c2);
        prof.addClass(c3);

        em.persist(math);
        em.persist(c1);
        em.persist(c2);
        em.persist(c3);
        em.persist(prof);
        em.getTransaction().commit();
        em.clear();

        Professor found = em.createQuery(
                "SELECT p FROM Professor p JOIN FETCH p.classes WHERE p.username = :u", Professor.class)
                .setParameter("u", "prof.ahmed")
                .getSingleResult();

        assertEquals("Mathématiques", found.getSubject().getName());
        assertEquals(3, found.getClasses().size());
        assertTrue(found.getClasses().stream()
                .anyMatch(c -> c.getName().equals("2ème Info")));
    }

    @Test
    @Order(4)
    @DisplayName("Test Admin creation")
    void testAdmin() {
        Admin admin = new Admin("admin", "admin123", "Super Admin", "admin@school.ma");

        em.persist(admin);
        em.getTransaction().commit();
        em.clear();

        Admin found = em.find(Admin.class, admin.getId());
        assertEquals("Super Admin", found.getFullName());
        assertTrue(found.isActive());
    }

    @Test
    @Order(5)
    @DisplayName("Test Note (Grade)")
    void testNote() {
        Major info = new Major("INFO", "Informatique", 2);
        em.persist(info);
        Classe classe = new Classe("1ère Info","dh",29);
        classe.setMajor(info);
        Student s = new Student("GA111", "ali2005", "pass", "Ali", "Rahali", LocalDate.of(2005, 1, 1));
        Subject math = new Subject("MATH", 6);
        s.setClasse(classe);

        Note note = new Note(s, math, 18.5, "DS1");

        em.persist(classe);
        em.persist(math);
        em.persist(s);
        em.persist(note);
        em.getTransaction().commit();
        em.clear();

        List<Note> notes = em.createQuery("FROM Note", Note.class).getResultList();
        assertEquals(1, notes.size());
        assertEquals(18.5, notes.get(0).getValue());
    }
}