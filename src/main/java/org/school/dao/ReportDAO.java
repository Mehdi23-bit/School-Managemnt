package org.school.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.school.entities.Report;
import org.school.config.HibernateUtil;

import java.time.LocalDateTime;
import java.util.List;

public class ReportDAO {

    // ==================== CREATE ====================
    public void saveReport(Report report) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(report);
            transaction.commit();
            System.out.println("✓ Report submitted: " + report.getTitle());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // ==================== READ ====================
    
    public Report getReportById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Report.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Get all reports by professor
    public List<Report> getReportsByProfessor(Long professorId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Report WHERE professor.id = :professorId ORDER BY createdAt DESC";
            Query<Report> query = session.createQuery(hql, Report.class);
            query.setParameter("professorId", professorId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Get all reports by student
    public List<Report> getReportsByStudent(Long studentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Report WHERE student.id = :studentId ORDER BY createdAt DESC";
            Query<Report> query = session.createQuery(hql, Report.class);
            query.setParameter("studentId", studentId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Get reports by professor and status
    public List<Report> getReportsByProfessorAndStatus(Long professorId, String status) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Report WHERE professor.id = :professorId AND status = :status ORDER BY createdAt DESC";
            Query<Report> query = session.createQuery(hql, Report.class);
            query.setParameter("professorId", professorId);
            query.setParameter("status", status);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Get all pending reports (for admin)
    public List<Report> getPendingReports() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Report WHERE status = 'PENDING' ORDER BY createdAt ASC";
            return session.createQuery(hql, Report.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ==================== UPDATE ====================
    
    public void updateReport(Report report) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            report.setUpdatedAt(LocalDateTime.now());
            session.merge(report);
            transaction.commit();
            System.out.println("✓ Report updated: " + report.getTitle());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Update report status
    public void updateReportStatus(Long reportId, String newStatus) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Report report = session.get(Report.class, reportId);
            if (report != null) {
                report.setStatus(newStatus);
                report.setUpdatedAt(LocalDateTime.now());
                session.merge(report);
                transaction.commit();
                System.out.println("✓ Report status updated to: " + newStatus);
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // ==================== DELETE ====================
    
    public void deleteReport(Long reportId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Report report = session.get(Report.class, reportId);
            if (report != null) {
                session.remove(report);
                transaction.commit();
                System.out.println("✓ Report deleted");
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Count pending reports
    public Long countPendingReports() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(r) FROM Report r WHERE status = 'PENDING'";
            return session.createQuery(hql, Long.class).uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }
}