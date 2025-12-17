package org.school.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.school.entities.Major;
import org.school.entities.Classe;
import org.school.config.HibernateUtil;

import java.util.List;

public class MajorDAO {
    
    // ==================== CREATE ====================
    public void saveMajor(Major major) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(major);
            transaction.commit();
            System.out.println("✓ Major saved: " + major.getName());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    
    // ==================== READ ====================
    
    // Get major by ID
    public Major getMajorById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Major.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get major by code
    public Major getMajorByCode(String code) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Major WHERE code = :code";
            Query<Major> query = session.createQuery(hql, Major.class);
            query.setParameter("code", code.toUpperCase());
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get major by name
    public Major getMajorByName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Major WHERE name = :name";
            Query<Major> query = session.createQuery(hql, Major.class);
            query.setParameter("name", name);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get all majors
    public List<Major> getAllMajors() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Major ORDER BY name";
            return session.createQuery(hql, Major.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get active majors only
    public List<Major> getActiveMajors() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Major WHERE isActive = true ORDER BY name";
            return session.createQuery(hql, Major.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get inactive majors
    public List<Major> getInactiveMajors() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Major WHERE isActive = false ORDER BY name";
            return session.createQuery(hql, Major.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get majors by duration
    public List<Major> getMajorsByDuration(int durationYears) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Major WHERE durationYears = :duration ORDER BY name";
            Query<Major> query = session.createQuery(hql, Major.class);
            query.setParameter("duration", durationYears);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get major with its classes (fetch join to avoid lazy loading issues)
    public Major getMajorWithClasses(Long majorId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT m FROM Major m LEFT JOIN FETCH m.classes WHERE m.id = :majorId";
            Query<Major> query = session.createQuery(hql, Major.class);
            query.setParameter("majorId", majorId);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get classes in a major
    public List<Classe> getClassesInMajor(Long majorId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT c FROM Classe c WHERE c.major.id = :majorId ORDER BY c.name";
            Query<Classe> query = session.createQuery(hql, Classe.class);
            query.setParameter("majorId", majorId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get majors that have classes
    public List<Major> getMajorsWithClasses() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT DISTINCT m FROM Major m JOIN m.classes";
            return session.createQuery(hql, Major.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get majors without classes
    public List<Major> getMajorsWithoutClasses() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT m FROM Major m WHERE m.classes IS EMPTY";
            return session.createQuery(hql, Major.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get majors ordered by number of classes
    public List<Major> getMajorsOrderedByClassCount() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT m FROM Major m ORDER BY SIZE(m.classes) DESC";
            return session.createQuery(hql, Major.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // ==================== UPDATE ====================
    
    public void updateMajor(Major major) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(major);
            transaction.commit();
            System.out.println("✓ Major updated: " + major.getName());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    
    // Activate/Deactivate major
    public void setMajorActive(Long majorId, boolean isActive) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Major major = session.get(Major.class, majorId);
            if (major != null) {
                major.setActive(isActive);
                session.merge(major);
                transaction.commit();
                System.out.println("✓ Major " + (isActive ? "activated" : "deactivated") + ": " + major.getName());
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    
    // Update major duration
    public void updateMajorDuration(Long majorId, int newDuration) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Major major = session.get(Major.class, majorId);
            if (major != null) {
                major.setDurationYears(newDuration);
                session.merge(major);
                transaction.commit();
                System.out.println("✓ Major duration updated: " + major.getName() + " -> " + newDuration + " years");
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    
    // Update major description
    public void updateMajorDescription(Long majorId, String newDescription) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Major major = session.get(Major.class, majorId);
            if (major != null) {
                major.setDescription(newDescription);
                session.merge(major);
                transaction.commit();
                System.out.println("✓ Major description updated: " + major.getName());
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    
    // ==================== DELETE ====================
    
    public void deleteMajor(Long majorId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Major major = session.get(Major.class, majorId);
            if (major != null) {
                session.remove(major);
                transaction.commit();
                System.out.println("✓ Major deleted: " + major.getName());
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    
    // Delete major only if it has no classes
    public boolean deleteMajorIfEmpty(Long majorId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Major major = session.get(Major.class, majorId);
            if (major != null && major.getClasses().isEmpty()) {
                session.remove(major);
                transaction.commit();
                System.out.println("✓ Major deleted: " + major.getName());
                return true;
            } else {
                System.out.println("✗ Cannot delete major: has classes assigned");
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
    
    // Soft delete (deactivate instead of delete)
    public void softDeleteMajor(Long majorId) {
        setMajorActive(majorId, false);
    }
    
    // ==================== SEARCH & FILTER ====================
    
    // Search majors by name (partial match)
    public List<Major> searchMajorsByName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Major WHERE name LIKE :name";
            Query<Major> query = session.createQuery(hql, Major.class);
            query.setParameter("name", "%" + name + "%");
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Search majors by code (partial match)
    public List<Major> searchMajorsByCode(String code) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Major WHERE code LIKE :code";
            Query<Major> query = session.createQuery(hql, Major.class);
            query.setParameter("code", "%" + code.toUpperCase() + "%");
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Count total majors
    public Long countMajors() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(m) FROM Major m";
            return session.createQuery(hql, Long.class).uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }
    
    // Count active majors
    public Long countActiveMajors() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(m) FROM Major m WHERE m.isActive = true";
            return session.createQuery(hql, Long.class).uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }
    
    // Count classes in a major
    public Long countClassesInMajor(Long majorId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(c) FROM Classe c WHERE c.major.id = :majorId";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("majorId", majorId);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }
    
    // Check if major code exists
    public boolean majorCodeExists(String code) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(m) FROM Major m WHERE m.code = :code";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("code", code.toUpperCase());
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Check if major name exists
    public boolean majorNameExists(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(m) FROM Major m WHERE m.name = :name";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("name", name);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get average duration of all majors
    public Double getAverageDuration() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT AVG(m.durationYears) FROM Major m";
            return session.createQuery(hql, Double.class).uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }
    
    // Get major with most classes
    public Major getMajorWithMostClasses() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT m FROM Major m ORDER BY SIZE(m.classes) DESC";
            Query<Major> query = session.createQuery(hql, Major.class);
            query.setMaxResults(1);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get majors by duration range
    public List<Major> getMajorsByDurationRange(int minYears, int maxYears) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Major WHERE durationYears BETWEEN :minYears AND :maxYears ORDER BY durationYears, name";
            Query<Major> query = session.createQuery(hql, Major.class);
            query.setParameter("minYears", minYears);
            query.setParameter("maxYears", maxYears);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}