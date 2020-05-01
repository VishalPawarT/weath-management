package com.sivalabs.jcart.admin.web.dao;
// Generated 2 Sep, 2018 1:04:53 AM by Hibernate Tools 4.3.5.Final


import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.sivalabs.jcart.admin.web.models.CreditDetails;
import com.sivalabs.jcart.admin.web.models.DebitDetails;

/**
 * Home object for domain model class DebitDetails.
 * @see com.jetsynthesys.DebitDetails
 * @author Hibernate Tools
 */

@Repository
@Transactional
public class DebitDetailsHome {

	private static final Log log = LogFactory.getLog(DebitDetailsHome.class);

	@PersistenceContext
	private EntityManager entityManager;

	public void persist(DebitDetails transientInstance) {
		log.debug("persisting DebitDetails instance");
		try {
			entityManager.persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void remove(DebitDetails persistentInstance) {
		log.debug("removing DebitDetails instance");
		try {
			entityManager.remove(persistentInstance);
			log.debug("remove successful");
		} catch (RuntimeException re) {
			log.error("remove failed", re);
			throw re;
		}
	}

	public DebitDetails merge(DebitDetails detachedInstance) {
		log.debug("merging DebitDetails instance");
		try {
			DebitDetails result = entityManager.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public DebitDetails findById(int id) {
		log.debug("getting DebitDetails instance with id: " + id);
		try {
			DebitDetails instance = entityManager.find(DebitDetails.class, id);
			log.debug("get successful");
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<DebitDetails> getAllDebitTransactionDetails(int accountId) {
		log.debug("getting DebitDetails instance with id: " + accountId);
		try {
			List<DebitDetails> instance = entityManager.createQuery("From DebitDetails deb where deb.debTransactionAccountId = :accountId" ,DebitDetails.class)
					.setParameter("accountId", accountId)
					.getResultList();
			log.debug("get successful");
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public double getTotalDebitAmountForUser(int accountId) {
		log.debug("getting DebitDetails instance with id: " + accountId);
		try {
			double instance = (double) entityManager.createQuery("Select sum(deb.debTransactionAmount) From DebitDetails deb where deb.debTransactionAccountId = :accountId")
					.setParameter("accountId", accountId)
					.getSingleResult();
			log.debug("get successful");
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
}
