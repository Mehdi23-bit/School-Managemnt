package org.school.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.school.entities.Admin;
import org.school.config.HibernateUtil;

import java.util.List;

public class AdminDAO {
    
    // ==================== CREATE ====================
    public void saveAdmin(Admin admin) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(admin);
            transaction.commit();
            System.out.println("✓ Admin saved: " + admin.getUsername());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    
    // ==================== READ ====================
    
    // Get admin by ID
    public Admin getAdminById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Admin.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get admin by username
    public Admin getAdminByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Admin WHERE username = :username";
            Query<Admin> query = session.createQuery(hql, Admin.class);
            query.setParameter("username", username);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get admin by credentials (for login)
    public Admin getAdminByCredentials(String username, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Admin WHERE username = :username AND password = :password";
            Query<Admin> query = session.createQuery(hql, Admin.class);
            query.setParameter("username", username);
            query.setParameter("password", password);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get all admins
    public List<Admin> getAllAdmins() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Admin";
            return session.createQuery(hql, Admin.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Get active admins only
    public List<Admin> getActiveAdmins() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Admin WHERE isActive = true";
            return session.createQuery(hql, Admin.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // ==================== UPDATE ====================
    
    public void updateAdmin(Admin admin) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(admin);
            transaction.commit();
            System.out.println("✓ Admin updated: " + admin.getUsername());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    
    // Activate/Deactivate admin
    public void toggleAdminStatus(Long adminId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Admin admin = session.get(Admin.class, adminId);
            if (admin != null) {
                admin.setActive(!admin.isActive());
                session.merge(admin);
                transaction.commit();
                System.out.println("✓ Admin status toggled: " + admin.getUsername());
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    
    // ==================== DELETE ====================
    
    public void deleteAdmin(Long adminId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Admin admin = session.get(Admin.class, adminId);
            if (admin != null) {
                session.remove(admin);
                transaction.commit();
                System.out.println("✓ Admin deleted: " + admin.getUsername());
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    
    // Soft delete (just deactivate)
    public void softDeleteAdmin(Long adminId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Admin admin = session.get(Admin.class, adminId);
            if (admin != null) {
                admin.setActive(false);
                session.merge(admin);
                transaction.commit();
                System.out.println("✓ Admin deactivated: " + admin.getUsername());
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    
    // ==================== SEARCH & FILTER ====================
    
    // Search admins by name
    public List<Admin> searchAdminsByName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Admin WHERE firstName LIKE :name OR lastName LIKE :name";
            Query<Admin> query = session.createQuery(hql, Admin.class);
            query.setParameter("name", "%" + name + "%");
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Count total admins
    public Long countAdmins() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(a) FROM Admin a";
            return session.createQuery(hql, Long.class).uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }
    
    // Check if username exists
    public boolean usernameExists(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(a) FROM Admin a WHERE a.username = :username";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("username", username);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}