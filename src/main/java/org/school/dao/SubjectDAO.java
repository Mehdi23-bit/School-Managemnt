package org.school.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.school.entities.Subject;
import org.school.entities.Professor;
import org.school.config.HibernateUtil;

import java.util.List;

public class SubjectDAO {
    
    // ==================== CREATE ====================
    public void saveSubject(Subject subject) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(subject);
            transaction.commit();
            System.out.println("✓ Subject saved: " + subject.getName());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    
    // ==================== READ ====================
    
    // Get subject by ID
    public Subject getSubjectById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Subject.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get subject by name
    public Subject getSubjectByName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Subject WHERE name = :name";
            Query<Subject> query = session.createQuery(hql, Subject.class);
            query.setParameter("name", name);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get all subjects
    public List<Subject> getAllSubjects() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Subject ORDER BY name";
            return session.createQuery(hql, Subject.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get subjects ordered by coefficient
    public List<Subject> getSubjectsByCoefficient() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Subject ORDER BY coefficient DESC";
            return session.createQuery(hql, Subject.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get subjects with coefficient greater than specified value
    public List<Subject> getSubjectsByMinCoefficient(double minCoefficient) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Subject WHERE coefficient >= :minCoeff ORDER BY coefficient DESC";
            Query<Subject> query = session.createQuery(hql, Subject.class);
            query.setParameter("minCoeff", minCoefficient);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get subject with its professors (fetch join to avoid lazy loading issues)
    public Subject getSubjectWithProfessors(Long subjectId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT s FROM Subject s LEFT JOIN FETCH s.professors WHERE s.id = :subjectId";
            Query<Subject> query = session.createQuery(hql, Subject.class);
            query.setParameter("subjectId", subjectId);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get subjects that have at least one professor
    public List<Subject> getSubjectsWithProfessors() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT DISTINCT s FROM Subject s JOIN s.professors";
            return session.createQuery(hql, Subject.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get subjects without professors
    public List<Subject> getSubjectsWithoutProfessors() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT s FROM Subject s WHERE s.professors IS EMPTY";
            return session.createQuery(hql, Subject.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public List<Professor>  getProfessors(Long id){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT p FROM Professor p WHERE p.subject.id = :subjectId";
    Query<Professor> query = session.createQuery(hql, Professor.class);
    query.setParameter("subjectId", id);
    return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    // ==================== UPDATE ====================
    
    public void updateSubject(Subject subject) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(subject);
            transaction.commit();
            System.out.println("✓ Subject updated: " + subject.getName());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    
    // Update subject coefficient
    public void updateSubjectCoefficient(Long subjectId, double newCoefficient) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Subject subject = session.get(Subject.class, subjectId);
            if (subject != null) {
                subject.setCoefficient(newCoefficient);
                session.merge(subject);
                transaction.commit();
                System.out.println("✓ Subject coefficient updated: " + subject.getName() + " -> " + newCoefficient);
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    
    // ==================== DELETE ====================
    
    public void deleteSubject(Long subjectId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Subject subject = session.get(Subject.class, subjectId);
            if (subject != null) {
                session.remove(subject);
                transaction.commit();
                System.out.println("✓ Subject deleted: " + subject.getName());
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    
    // Delete subject only if it has no professors
    public boolean deleteSubjectIfEmpty(Long subjectId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Subject subject = session.get(Subject.class, subjectId);
            if (subject != null && getProfessors(subject.getId()).isEmpty()) {
                session.remove(subject);
                transaction.commit();
                System.out.println("✓ Subject deleted: " + subject.getName());
                return true;
            } else {
                System.out.println("✗ Cannot delete subject: has professors assigned");
                return false;
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }
    
    // ==================== SEARCH & FILTER ====================
    
    // Search subjects by name (partial match)
    public List<Subject> searchSubjectsByName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Subject WHERE name LIKE :name";
            Query<Subject> query = session.createQuery(hql, Subject.class);
            query.setParameter("name", "%" + name + "%");
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Count total subjects
    public Long countSubjects() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(s) FROM Subject s";
            return session.createQuery(hql, Long.class).uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }
    
    // Count professors for a subject
    public Long countProfessorsForSubject(Long subjectId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(p) FROM Professor p WHERE p.subject.id = :subjectId";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("subjectId", subjectId);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }
    
    // Check if subject name exists
    public boolean subjectNameExists(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(s) FROM Subject s WHERE s.name = :name";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("name", name);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get average coefficient of all subjects
    public Double getAverageCoefficient() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT AVG(s.coefficient) FROM Subject s";
            return session.createQuery(hql, Double.class).uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }
    
    // Get subject with highest coefficient
    public Subject getSubjectWithHighestCoefficient() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Subject ORDER BY coefficient DESC";
            Query<Subject> query = session.createQuery(hql, Subject.class);
            query.setMaxResults(1);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}