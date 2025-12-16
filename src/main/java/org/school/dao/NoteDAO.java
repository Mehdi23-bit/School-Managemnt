package org.school.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.school.config.HibernateUtil;
import org.school.entities.Note;

public class NoteDAO {

    // ==================== CREATE ====================
    public void saveNote(Note note) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(note);
            transaction.commit();
            System.out.println("✓ Note saved for " + note.getStudent().getFullName());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // ==================== READ ====================
    
    public Note getNoteById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Note.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Get notes by student
    public List<Note> getNotesByStudent(Long studentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Note WHERE student.id = :studentId ORDER BY givenAt DESC";
            Query<Note> query = session.createQuery(hql, Note.class);
            query.setParameter("studentId", studentId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Get notes by student and subject
    public List<Note> getNotesByStudentAndSubject(Long studentId, Long subjectId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Note WHERE student.id = :studentId AND subject.id = :subjectId ORDER BY givenAt DESC";
            Query<Note> query = session.createQuery(hql, Note.class);
            query.setParameter("studentId", studentId);
            query.setParameter("subjectId", subjectId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Get all notes for a class by subject (for a professor)
    public List<Note> getNotesByClassAndSubject(Long classId, Long subjectId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Note WHERE student.classe.id = :classId AND subject.id = :subjectId ORDER BY student.lastName";
            Query<Note> query = session.createQuery(hql, Note.class);
            query.setParameter("classId", classId);
            query.setParameter("subjectId", subjectId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Get average note for a student in a subject
    public Double getAverageNoteByStudentAndSubject(Long studentId, Long subjectId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT AVG(n.value) FROM Note n WHERE n.student.id = :studentId AND n.subject.id = :subjectId";
            Query<Double> query = session.createQuery(hql, Double.class);
            query.setParameter("studentId", studentId);
            query.setParameter("subjectId", subjectId);
            Double result = query.uniqueResult();
            return result != null ? result : 0.0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    // ==================== UPDATE ====================
    
    public void updateNote(Note note) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(note);
            transaction.commit();
            System.out.println("✓ Note updated for " + note.getStudent().getFullName());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // ==================== DELETE ====================
    
    public void deleteNote(Long noteId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Note note = session.get(Note.class, noteId);
            if (note != null) {
                session.remove(note);
                transaction.commit();
                System.out.println("✓ Note deleted");
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Count total notes
    public Long countNotes() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(n) FROM Note n";
            return session.createQuery(hql, Long.class).uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }
}