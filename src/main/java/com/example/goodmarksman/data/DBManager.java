package com.example.goodmarksman.data;

import com.example.goodmarksman.Server.HibernateSessionFactoryUtil;
import com.example.goodmarksman.objects.Score;
import org.hibernate.Session;

import java.sql.*;
import java.util.ArrayList;

public class DBManager extends DAO {

    Connection c;

    public DBManager() {
        connect();
    }

    private void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(
                    "jdbc:sqlite:players_score.db");
            System.out.println("Opened database successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC not found: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("DB connection err: " + e.getMessage());
        }
    }

    public void insertScore(Score score) throws Exception {
        try {
            Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();

            ArrayList<Score> scores = getScoreBord(score.getPlayerName());
            if (scores.size() > 1) {
                session.close();
                throw new Exception("Find to many players with name " + score.getPlayerName());
            }

            session.beginTransaction();
            if (!scores.isEmpty() && scores.get(0).getShotCountValue() > score.getShotCountValue()) {
                score.setId(scores.get(0).getId());
                session.update(score);
            } else if (scores.isEmpty()) session.persist(score);

            session.getTransaction().commit();
            session.close();

        } catch (Exception e) {
            System.err.println("Insert score err: " + e.getMessage());
            throw e;
        }
    }

    public ArrayList<Score> getScoreBord(String playerName) {
        if (playerName.isEmpty()) return null;

        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        ArrayList<Score> scores;

        if (playerName.equals("*")) {
            scores = (ArrayList<Score>) session
                    .createQuery("FROM Score", Score.class).list();
        } else {
            scores = (ArrayList<Score>) session
                    .createQuery("FROM Score WHERE playerName LIKE :playerName", Score.class)
                    .setParameter("playerName", playerName).list();
        }

//        System.out.println(scores);

        session.close();
        return scores;
    }
}
