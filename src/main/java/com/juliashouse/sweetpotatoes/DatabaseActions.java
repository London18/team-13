package com.juliashouse.sweetpotatoes;

import com.juliashouse.sweetpotatoes.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DatabaseActions {
    @Autowired
    private JdbcTemplate template;

    public void postAction(String actionType, String actionTime,
                           String comment, int SCID, String sessionId) {
        template.execute(
                String.format("INSERT INTO VISIT_UPDATES VALUES(NULL, " +
                        "%s, %s, %s, %s)", SCID, actionType, actionTime, comment));

    }

    public Family getFamily(int familyId) {
        Family family;
        String sql = "SELECT * FROM FAMILIES WHERE familyID = " + familyId;
        family = template.queryForObject(sql, new Object[]{}, new RowMapper<Family>() {
            public Family mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                return new Family(resultSet.getString("name"), resultSet.getString("address"));
            }
        });
        return family;
    }

    public ScheduleEvent getScheduleEventById(int id) {
        String sql = String.format("SELECT * FROM ScheduleEvent WHERE ScheduleID = " + id, id);
        return template.queryForObject(sql, new RowMapper<ScheduleEvent>() {
            @Override
            public ScheduleEvent mapRow(ResultSet resultSet, int i) throws SQLException {
                return new ScheduleEvent(resultSet.getDate("dateStart"), resultSet.getDate("dateEnd"), getFamily(resultSet.getInt("familyId")));
            }
        });
    }

    public Carer getCarerById(int id) {
        String sql = String.format("SELECT * FROM ScheduleEvent WHERE ScheduleID = " + id, id);
        return template.queryForObject(sql, new RowMapper<Carer>() {
            @Override
            public Carer mapRow(ResultSet resultSet, int i) throws SQLException {
                return new Carer(new User(""), resultSet.getString("address"),
                        resultSet.getString("phone"), resultSet.getString("firstName"), resultSet.getString("lastName"));
            }
        });
    }

    public List<VisitUpdate> getVisitUpdates(int scheduleId) {
        String sql = String.format("SELECT * FROM SCHEDULE_CARER JOIN VISIT_UPDATE " +
                "ON SCHEDULE_CARER.ScheduleCarerID = VISIT_UPDATE.ScheduleCarerID" +
                " WHERE ScheduleID = %s", scheduleId);

        List<VisitUpdate> visitUpdates;
        visitUpdates = template.query(sql, new Object[]{}, new RowMapper<VisitUpdate>() {
            @Override
            public VisitUpdate mapRow(ResultSet resultSet, int i) throws SQLException {
                return new VisitUpdate(null,            // TODO
                        resultSet.getString("action"), resultSet.getDate("time"),
                        resultSet.getString("comments"));
            }
        });

        return visitUpdates;

    }

    public List<ScheduleEvent> getSchedule(int carerId, String date, String SessionId) {
        String sql = String.format("SELECT * FROM SCHEDULE_CARER JOIN SCHEDULE_EVENT ON SCHEDULE_CARER.CarerID = SCHEDULE_EVENT.ScheduleID WHERE CarerID = %s",
                carerId);
        List<ScheduleEvent> scheduleEvents;
        scheduleEvents = template.query(sql, new Object[]{}, new RowMapper<ScheduleEvent>() {
            @Override
            public ScheduleEvent mapRow(ResultSet resultSet, int i) throws SQLException {
                return new ScheduleEvent(resultSet.getDate("dateStart"), resultSet.getDate("dateEnd"), getFamily(resultSet.getInt("familyId")));
            }
        });
        return scheduleEvents;
    }


}
