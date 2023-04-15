package com.company.enroller.persistence;

import com.company.enroller.model.Participant;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component("participantService")
public class ParticipantService {

	DatabaseConnector connector;

	@Autowired //automatyczna inicjalizacja instancji obiektu poprzez Bean w klasie App
	PasswordEncoder passwordEncoder;

	public ParticipantService() {
		connector = DatabaseConnector.getInstance();
	}

	public Collection<Participant> getAll() {
		String hql = "FROM Participant";
		return connector.getSession().createQuery(hql).list();
	}

	public Collection<Participant> getAll(String sortBy, String sortOrder, String key) {
		String hql = "FROM Participant";
		if (!key.equals("")) {
			hql += " WHERE login LIKE '%" + key + "%'";
		}
		if (sortBy.equals("login")) {
			hql += " ORDER BY login";
			if (sortOrder.equals("ASC") || sortOrder.equals("DESC")) {
				hql += " " +  sortOrder;
			}
		}
		Query query = connector.getSession().createQuery(hql);
		return query.list();
	}

	public Participant findByLogin(String login) {
		return connector.getSession().get(Participant.class, login);
	}

	public Participant add(Participant participant) {
		String hashedPassword = passwordEncoder.encode(participant.getPassword());
		participant.setPassword(hashedPassword);

		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().save(participant);
		transaction.commit();
		return participant;
	}

	public void update(Participant participant) {
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().merge(participant);
		transaction.commit();
	}

	public void delete(Participant participant) {
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().delete(participant);
		transaction.commit();
	}

}
