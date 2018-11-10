package com.juliashouse.sweetpotatoes;

import com.juliashouse.sweetpotatoes.entity.Family;
import com.juliashouse.sweetpotatoes.entity.ScheduleEvent;
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

    public List<ScheduleEvent> getSchedule(String carerId, String date, String SessionId) {
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
