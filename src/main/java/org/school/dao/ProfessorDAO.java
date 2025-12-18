package org.school.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.school.entities.Classe;
import org.school.entities.Professor;
import org.school.entities.Subject;
import org.school.config.HibernateUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProfessorDAO {
    
    // ==================== CREATE ====================
    public void saveProfessor(Professor professor) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(professor);
            transaction.commit();
            System.out.println("✓ Professor saved: " + professor.getUsername());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    
    // ==================== READ ====================
    
    // Get professor by ID
    public Professor getProfessorById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Professor.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get professor by username
    public Professor getProfessorByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Professor WHERE username = :username";
            Query<Professor> query = session.createQuery(hql, Professor.class);
            query.setParameter("username", username);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get professor by credentials (for login)
    public Professor getProfessorByCredentials(String username, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Professor WHERE username = :username AND password = :password";
            Query<Professor> query = session.createQuery(hql, Professor.class);
            query.setParameter("username", username);
            query.setParameter("password", password);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get all professors
    public List<Professor> getAllProfessors() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Professor";
            return session.createQuery(hql, Professor.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get active professors only
    public List<Professor> getActiveProfessors() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Professor WHERE isActive = true";
            return session.createQuery(hql, Professor.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get professors by subject
    public List<Professor> getProfessorsBySubject(Long subjectId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Professor WHERE subject.id = :subjectId";
            Query<Professor> query = session.createQuery(hql, Professor.class);
            query.setParameter("subjectId", subjectId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get professors by class
    public List<Professor> getProfessorsByClass(Long classId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT p FROM Professor p JOIN p.classes c WHERE c.id = :classId";
            Query<Professor> query = session.createQuery(hql, Professor.class);
            query.setParameter("classId", classId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<Classe> getClassesByProfessor(Long professorId) {
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
        String hql = "SELECT c FROM Classe c JOIN c.professors p WHERE p.id = :professorId";
        Query<Classe> query = session.createQuery(hql, Classe.class);
        query.setParameter("professorId", professorId);
        return query.list();
    } catch (Exception e) {
        e.printStackTrace();
        return new ArrayList<>();
    }
}
// Check if a class already has a professor for a specific subject
// Check if a class already has a professor for a specific subject
public Professor getProfessorByClasseAndSubject(Long classeId, Long subjectId) {
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
        String hql = "SELECT p FROM Professor p " +
                     "JOIN p.classes c " +
                     "WHERE c.id = :classeId AND p.subject.id = :subjectId";
        
        Query<Professor> query = session.createQuery(hql, Professor.class);
        query.setParameter("classeId", classeId);
        query.setParameter("subjectId", subjectId);
        
        return query.uniqueResult();
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}


    // ==================== UPDATE ====================
    
    public void updateProfessor(Professor professor) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(professor);
            transaction.commit();
            System.out.println("✓ Professor updated: " + professor.getUsername());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    
    // Activate/Deactivate professor
    public void toggleProfessorStatus(Long professorId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Professor professor = session.get(Professor.class, professorId);
            if (professor != null) {
                professor.setActive(!professor.isActive());
                session.merge(professor);
                transaction.commit();
                System.out.println("✓ Professor status toggled: " + professor.getUsername());
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    
    // ==================== DELETE ====================
    
    public void deleteProfessor(Long professorId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Professor professor = session.get(Professor.class, professorId);
            if (professor != null) {
                session.remove(professor);
                transaction.commit();
                System.out.println("✓ Professor deleted: " + professor.getUsername());
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    
    // Soft delete (just deactivate)
    public void softDeleteProfessor(Long professorId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Professor professor = session.get(Professor.class, professorId);
            if (professor != null) {
                professor.setActive(false);
                session.merge(professor);
                transaction.commit();
                System.out.println("✓ Professor deactivated: " + professor.getUsername());
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    
    // ==================== SEARCH & FILTER ====================
    
    // Search professors by name
    public List<Professor> searchProfessorsByName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Professor WHERE firstName LIKE :name OR lastName LIKE :name";
            Query<Professor> query = session.createQuery(hql, Professor.class);
            query.setParameter("name", "%" + name + "%");
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Count total professors
    public Long countProfessors() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(p) FROM Professor p";
            return session.createQuery(hql, Long.class).uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }
    
    // Check if username exists
    public boolean usernameExists(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(p) FROM Professor p WHERE p.username = :username";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("username", username);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isSubjectAlreadyTaughtInClasse(Long classeId, Long subjectId, Long excludeProfessorId) {
        Professor existingProfessor = getProfessorByClasseAndSubject(classeId, subjectId);
        
        if (existingProfessor == null) {
            return false; // No professor teaching this subject in this class
        }
        
        // If we're updating an existing assignment, exclude the current professor
        if (excludeProfessorId != null && existingProfessor.getId().equals(excludeProfessorId)) {
            return false;
        }
        
        return true; // Another professor is already teaching this subject
    }
    
}