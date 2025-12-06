package org.school;

//hibernate imports
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.school.entities.Student;
import org.school.config.HibernateUtil;
//javafx import

public class App {
    private static final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    public static void main(String[] args) {

        // ─────── FIRST TRANSACTION ───────
        Session session1 = sessionFactory.getCurrentSession();
        session1.beginTransaction();

        Student s1 = new Student();

        s1.setFirstName("Mehdi");
        session1.save(s1);

        session1.getTransaction().commit();   // ← session1 is now AUTO-CLOSED

        // ─────── SECOND TRANSACTION (new session) ───────
        Session session2 = sessionFactory.getCurrentSession();  // new one!
        session2.beginTransaction();

        Student s2 = new Student();
        s2.setFirstName("Ahmed");
        session2.save(s2);

        session2.getTransaction().commit();   // ← session2 closed

        // ─────── If you want to query ───────
       

        // Close factory at the very end
        sessionFactory.close();
    }
}