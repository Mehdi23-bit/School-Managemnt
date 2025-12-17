package org.school.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.school.entities.Classe;
import org.school.entities.Student;
import org.school.entities.Professor;
import org.school.entities.Major;
import org.school.config.HibernateUtil;

import java.util.List;

public class ClasseDAO {
    
    // ==================== CREATE ====================
    public void saveClasse(Classe classe) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(classe);
            transaction.commit();
            System.out.println("✓ Classe saved: " + classe.getName());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    
    // ==================== READ ====================
    
    // Get classe by ID
    public Classe getClasseById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Classe.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get classe by name
    public Classe getClasseByName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Classe WHERE name = :name";
            Query<Classe> query = session.createQuery(hql, Classe.class);
            query.setParameter("name", name);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get all classes
    public List<Classe> getAllClasses() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Classe ORDER BY name";
            return session.createQuery(hql, Classe.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get classes by level
    public List<Classe> getClassesByLevel(String level) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Classe WHERE level = :level ORDER BY name";
            Query<Classe> query = session.createQuery(hql, Classe.class);
            query.setParameter("level", level);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get classes by major
    public List<Classe> getClassesByMajor(Long majorId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Classe WHERE major.id = :majorId ORDER BY name";
            Query<Classe> query = session.createQuery(hql, Classe.class);
            query.setParameter("majorId", majorId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get classe with its students (fetch join to avoid lazy loading issues)
    public Classe getClasseWithStudents(Long classeId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT c FROM Classe c LEFT JOIN FETCH c.students WHERE c.id = :classeId";
            Query<Classe> query = session.createQuery(hql, Classe.class);
            query.setParameter("classeId", classeId);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get classe with its professors
    public Classe getClasseWithProfessors(Long classeId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT c FROM Classe c LEFT JOIN FETCH c.professors WHERE c.id = :classeId";
            Query<Classe> query = session.createQuery(hql, Classe.class);
            query.setParameter("classeId", classeId);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get students in a classe
    public List<Student> getStudentsInClasse(Long classeId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT s FROM Student s WHERE s.classe.id = :classeId ORDER BY s.lastName, s.firstName";
            Query<Student> query = session.createQuery(hql, Student.class);
            query.setParameter("classeId", classeId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get professors teaching a classe
    public List<Professor> getProfessorsInClasse(Long classeId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT p FROM Professor p JOIN p.classes c WHERE c.id = :classeId";
            Query<Professor> query = session.createQuery(hql, Professor.class);
            query.setParameter("classeId", classeId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get classes that are full (students count >= capacity)
    public List<Classe> getFullClasses() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT c FROM Classe c WHERE SIZE(c.students) >= c.capacity";
            return session.createQuery(hql, Classe.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get classes with available spots
    public List<Classe> getClassesWithAvailableSpots() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT c FROM Classe c WHERE SIZE(c.students) < c.capacity";
            return session.createQuery(hql, Classe.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get classes without students
    public List<Classe> getEmptyClasses() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT c FROM Classe c WHERE c.students IS EMPTY";
            return session.createQuery(hql, Classe.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // ==================== UPDATE ====================
    
    public void updateClasse(Classe classe) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(classe);
            transaction.commit();
            System.out.println("✓ Classe updated: " + classe.getName());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    
    // Update classe capacity
    public void updateClasseCapacity(Long classeId, int newCapacity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Classe classe = session.get(Classe.class, classeId);
            if (classe != null) {
                classe.setCapacity(newCapacity);
                session.merge(classe);
                transaction.commit();
                System.out.println("✓ Classe capacity updated: " + classe.getName() + " -> " + newCapacity);
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    
    // Update classe level
    public void updateClasseLevel(Long classeId, String newLevel) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Classe classe = session.get(Classe.class, classeId);
            if (classe != null) {
                classe.setLevel(newLevel);
                session.merge(classe);
                transaction.commit();
                System.out.println("✓ Classe level updated: " + classe.getName() + " -> " + newLevel);
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    
    // ==================== DELETE ====================
    
    public void deleteClasse(Long classeId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Classe classe = session.get(Classe.class, classeId);
            if (classe != null) {
                session.remove(classe);
                transaction.commit();
                System.out.println("✓ Classe deleted: " + classe.getName());
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    
    // Delete classe only if it's empty
    public boolean deleteClasseIfEmpty(Long classeId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Classe classe = session.get(Classe.class, classeId);
            if (classe != null && classe.getStudents().isEmpty()) {
                session.remove(classe);
                transaction.commit();
                System.out.println("✓ Classe deleted: " + classe.getName());
                return true;
            } else {
                System.out.println("✗ Cannot delete classe: has students assigned");
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
    
    // Search classes by name (partial match)
    public List<Classe> searchClassesByName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Classe WHERE name LIKE :name";
            Query<Classe> query = session.createQuery(hql, Classe.class);
            query.setParameter("name", "%" + name + "%");
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Count total classes
    public Long countClasses() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(c) FROM Classe c";
            return session.createQuery(hql, Long.class).uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }
    
    // Count students in a classe
    public Long countStudentsInClasse(Long classeId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(s) FROM Student s WHERE s.classe.id = :classeId";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("classeId", classeId);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }
    
    // Get available spots in classe
    public int getAvailableSpots(Long classeId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Classe classe = session.get(Classe.class, classeId);
            if (classe != null) {
                return classe.getCapacity() - classe.getStudents().size();
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    // Check if classe name exists
    public boolean classeNameExists(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(c) FROM Classe c WHERE c.name = :name";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("name", name);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Check if classe is full
    public boolean isClasseFull(Long classeId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Classe classe = session.get(Classe.class, classeId);
            if (classe != null) {
                return classe.getStudents().size() >= classe.getCapacity();
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get average class capacity
    public Double getAverageCapacity() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT AVG(c.capacity) FROM Classe c";
            return session.createQuery(hql, Double.class).uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }
    
    // Get classe with most students
    public Classe getClasseWithMostStudents() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT c FROM Classe c ORDER BY SIZE(c.students) DESC";
            Query<Classe> query = session.createQuery(hql, Classe.class);
            query.setMaxResults(1);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get classes ordered by number of students
    public List<Classe> getClassesOrderedByStudentCount() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT c FROM Classe c ORDER BY SIZE(c.students) DESC";
            return session.createQuery(hql, Classe.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
