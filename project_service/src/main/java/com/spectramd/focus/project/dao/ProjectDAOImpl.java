package com.spectramd.focus.project.dao;

import com.spectramd.focus.project.entity.Project;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ProjectDAOImpl implements ProjectDAO {

    @Autowired
    private final DataSource dataSource;

    public ProjectDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private static final String GET_ALL_PROJECTS_QUERY = "select p.projectId,p.projectName,p.description,e.name as projectOwnerName ,p.projectOwnerId,p.startDate,p.endDate,p.isActive from project_staff p join employee_staff e on p.projectOwnerId = e.employeeId";

    private static final String GET_ALL_ACTIVE_PROJECTS_QUERY = "select p.projectId,p.projectName,p.description,e.name as projectOwnerName ,p.projectOwnerId,p.startDate,p.endDate,p.isActive from project_staff p join employee_staff e on p.projectOwnerId = e.employeeId where p.isActive=1";

    private static final String GET_PROJECT_BY_ID = "select p.projectId,p.projectName,p.description,e.name as projectOwnerName ,p.projectOwnerId,p.startDate,p.endDate,p.isActive from project_staff p join employee_staff e on p.projectOwnerId = e.employeeId where projectId=?";

    private static final String GET_PROJECT_BY_NAME = "select p.projectId,p.projectName,p.description,e.name as projectOwnerName,p.projectOwnerId,p.startDate,p.endDate,p.isActive from project_staff p join employee_staff e on p.projectOwnerId = e.employeeId where projectName like ?";

    private static final String GET_PROJECT_NAMES = "select projectName from project_staff where cast(projectName AS NCHAR) like ?";

    private static final String GET_PROJECT_IDS = "select projectId from project_staff where projectName like ?";

    private static final String ADD_PROJECT = "insert into project_staff(projectName,description,projectOwnerId,startDate,endDate,isActive) values(?,?,(select employeeId from employee_staff where name=?),?,?,?)";

    private static final String UPDATE_PROJECT_BY_ID = "update project_staff set projectName=?,description=?,projectOwnerId=(select employeeId from employee_staff where name=?),startDate=?,endDate=?,isActive=? where projectId=?";

    private static final String UPDATE_PROJECT_BY_NAME = "update project_staff set projectName=?,description=?,projectOwnerId=?,startDate=?,endDate=?,isActive=? where projectName like ?";

    private static final String DELETE_ALL_PROJECTS = "delete from project_staff";

    private static final String DELETE_PROJECT_BY_ID = "update project_staff set isActive=0 where projectId=?";

    private static final String PROJ_ID_ALLOCATION_EXISTS = "select count(*) from allocation_staff where projectId=?";

    private static final String PROJ_ID_IS_ACTIVE_CHECK = "select isActive from project_staff where projectId=?";

    private static final String DELETE_PROJECT_BY_NAME = "update project_staff set isActive=0 where projectName like ?";

    private static final String PROJ_NAME_ALLOCATION_EXISTS = "select count(*) from allocation_staff a join project_staff p on a.projectId = p.projectId where p.projectName=?";

    private static final String PROJ_NAME_IS_ACTIVE_CHECK = "select isActive from project_staff where projectName=?";

    private static final String PROJECT_ID_EXISTS = "select count(*) from project_staff where projectId=?";

    private static final String PROJECT_NAME_EXISTS = "select count(*) from project_staff where projectName like ?";

    private static final String PROJECT_EXISTS = "select count(*) from project_staff";

    private static final String PROJ_START_DATE_CONFLICT = "select count(*) from allocation_staff a join project_staff p on a.projectId = p.projectId where p.projectId=? and a.isActive=1 and a.allocationStartDate<?";

    private static final String PROJ_END_DATE_CONFLICT = "select count(*) from allocation_staff a join project_staff p on a.projectId = p.projectId where p.projectId=? and a.isActive=1 and a.allocationEndDate>?";

    @Override
    public List<Project> getAllProjects() {
        List<Project> projects = new ArrayList<>();

        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_ALL_PROJECTS_QUERY); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                projects.add(mapRowToProject(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving projects", e);
        }
        return projects;
    }

    /*
    @Override
    public List<Project> getAllProjects() {
        return jdbcTemplate.query(GET_ALL_PROJECTS_QUERY, new ProjectRowMapper());
    }
     */
    @Override
    public List<Project> getAllActiveProjects() {
        List<Project> projects = new ArrayList<>();

        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_ALL_ACTIVE_PROJECTS_QUERY); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                projects.add(mapRowToProject(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving active projects", e);
        }
        return projects;
    }

    @Override
    public Project getProjectById(int id) {
        Project project = null;

        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_PROJECT_BY_ID)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    project = mapRowToProject(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving project by ID", e);
        }

        return project;
    }

    /*
    @Override
    public Project getProjectById(int id) {
        List<Project> projects = jdbcTemplate.query(GET_PROJECT_BY_ID, new ProjectRowMapper(), id);
        return projects.isEmpty() ? null : projects.get(0);
    }
     */
    @Override
    public List<Project> getProjectByName(String name) {
        List<Project> projects = new ArrayList<>();
        String pattern = "%" + name + "%";
        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_PROJECT_BY_NAME)) {

            ps.setString(1, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    projects.add(mapRowToProject(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving project by Name", e);
        }

        return projects;
    }

    /*
    @Override
    public List<Project> getProjectByName(String name) {
        String pattern = "%" + name + "%";
        List<Project> projects = jdbcTemplate.query(GET_PROJECT_BY_NAME, new ProjectRowMapper(), pattern);
        return projects.isEmpty() ? null : projects;
    }
     */
    @Override
    public Project getProjectByNameForUpdate(String name) {
        Project project = null;
        String pattern = "%" + name + "%";
        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_PROJECT_BY_NAME)) {

            ps.setString(1, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    project = mapRowToProject(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving project by name for updation", e);
        }

        return project;
    }

    @Override
    public List<String> getProjectNames(String name) {

        List<String> names = new ArrayList<>();
        String pattern = "%" + name + "%";
        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_PROJECT_NAMES)) {
            ps.setString(1, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    names.add(rs.getString("projectName"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving project names", e);
        }

        return names;
    }

    @Override
    public List<Integer> getProjectIds(String name) {

        List<Integer> Ids = new ArrayList<>();
        String pattern = "%" + name + "%";
        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_PROJECT_IDS)) {
            ps.setString(1, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ids.add(rs.getInt("projectId"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving project Ids", e);
        }

        return Ids;
    }

    @Override
    public int addProject(Project project) {
        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(ADD_PROJECT)) {

            ps.setString(1, project.getProjectName());
            ps.setString(2, project.getDescription());
            ps.setString(3, project.getProjectOwnerName());
            ps.setString(4, project.getStartDate());
            ps.setString(5, project.getEndDate());
            ps.setBoolean(6, project.getIsActive());

            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error adding project", e);
        }
    }

    /*
    @Override
    public int addProject(Project project) {
        return jdbcTemplate.update(ADD_PROJECT, project.getProjectName(), project.getDescription(), project.getProjectOwnerId(),
                project.getStartDate(), project.getEndDate(), project.getIsActive());
    }
     */
    @Override
    public int updateProjectById(Project project) {
        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(UPDATE_PROJECT_BY_ID)) {

            ps.setString(1, project.getProjectName());
            ps.setString(2, project.getDescription());
            ps.setString(3, project.getProjectOwnerName());
            ps.setString(4, project.getStartDate());
            ps.setString(5, project.getEndDate());
            ps.setBoolean(6, project.getIsActive());
            ps.setInt(7, project.getProjectId());

            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating project by Id", e);
        }
    }

    /*
    @Override
    public int updateProjectById(Project project) {
        return jdbcTemplate.update(UPDATE_PROJECT_BY_ID, project.getProjectName(), project.getDescription(), project.getProjectOwnerId(), project.getStartDate(), project.getEndDate(), project.getIsActive(), project.getProjectId());
    }
     */
    @Override
    public int updateProjectByName(Project project) {
        String pattern = "%" + project.getProjectName() + "%";

        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(UPDATE_PROJECT_BY_NAME)) {

            ps.setString(1, project.getProjectName());
            ps.setString(2, project.getDescription());
            ps.setInt(3, project.getProjectOwnerId());
            ps.setString(4, project.getStartDate());
            ps.setString(5, project.getEndDate());
            ps.setBoolean(6, project.getIsActive());
            ps.setString(7, pattern);

            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating project by name", e);
        }
    }

    /*
    @Override
    public int updateProjectByName(Project project) {
        String pattern = "%" + project.getProjectName() + "%";
        return jdbcTemplate.update(UPDATE_PROJECT_BY_NAME, project.getProjectName(), project.getDescription(), project.getProjectOwnerId(), project.getStartDate(), project.getEndDate(), project.getIsActive(), pattern);
    }
     */
    @Override
    public int deleteAllProjects() {
        int rowsAffected = 0;

        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(DELETE_ALL_PROJECTS)) {

            rowsAffected = ps.executeUpdate();  // Execute the delete query

        } catch (SQLException e) {

            throw new RuntimeException("Error deleting all projects", e);
        }

        return rowsAffected;  // Return the number of rows deleted
    }

    @Override
    public int deleteProjectById(int id) {
        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(DELETE_PROJECT_BY_ID)) {

            ps.setInt(1, id);

            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting project by Id", e);
        }
    }

    @Override
    public boolean projIdAllocationExists(int id) {
        boolean exists = false;

        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(PROJ_ID_ALLOCATION_EXISTS)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if allocation for project ID exists", e);
        }
        return exists;
    }

    @Override
    public boolean projIdIsActiveCheck(int id) {

        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(PROJ_ID_IS_ACTIVE_CHECK)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("isActive");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking whether projectId is active", e);
        }
        return false;
    }

    /*
    @Override
    public int deleteProjectById(int id) {
        return jdbcTemplate.update(DELETE_PROJECT_BY_ID, id);
    }
     */
    @Override
    public int deleteProjectByName(String name) {
        String pattern = "%" + name + "%";

        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(DELETE_PROJECT_BY_NAME)) {

            ps.setString(1, pattern);

            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting project by name", e);
        }
    }

    @Override
    public boolean projNameAllocationExists(String name) {
        boolean exists = false;

        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(PROJ_NAME_ALLOCATION_EXISTS)) {

            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if allocation for project Name exists", e);
        }
        return exists;
    }

    @Override
    public boolean projNameIsActiveCheck(String name) {

        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(PROJ_NAME_IS_ACTIVE_CHECK)) {

            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("isActive");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking whether project name is active", e);
        }
        return false;
    }

    /*
    @Override
    public int deleteProjectByName(String name) {
        String pattern = "%" + name + "%";
        return jdbcTemplate.update(DELETE_PROJECT_BY_NAME, pattern);
    }
     */
    @Override
    public boolean projectIdExists(int id) {
        boolean exists = false;

        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(PROJECT_ID_EXISTS)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if project ID exists", e);
        }
        return exists;
    }

    /*
    @Override
    public boolean projectIdExists(int id) {
        Integer count = jdbcTemplate.queryForObject(PROJECT_ID_EXISTS, Integer.class, id);
        return count != null && count > 0;
    }
     */
    @Override
    public boolean projectNameExists(String name) {
        boolean exists = false;
        String pattern = "%" + name + "%";

        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(PROJECT_NAME_EXISTS)) {

            ps.setString(1, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if project Name exists", e);
        }
        return exists;
    }

    /*
    @Override
    public boolean projectNameExists(String name) {
        String pattern = "%" + name + "%";
        Integer count = jdbcTemplate.queryForObject(PROJECT_NAME_EXISTS, Integer.class, pattern);
        return count != null && count > 0;
    }
     */
    @Override
    public boolean projectExists() {
        boolean exists = false;

        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(PROJECT_EXISTS)) {

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if project exists", e);
        }
        return exists;
    }

    @Override
    public boolean checkProjStartDateConflict(int id, String newStartDate) {
        boolean conflict = false;

        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(PROJ_START_DATE_CONFLICT)) {

            ps.setInt(1, id);
            ps.setString(2, newStartDate);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    conflict = rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking Start Date conflict", e);
        }

        return conflict;
    }

    @Override
    public boolean checkProjEndDateConflict(int id, String newEndDate) {
        boolean conflict = false;

        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(PROJ_END_DATE_CONFLICT)) {

            ps.setInt(1, id);
            ps.setString(2, newEndDate);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    conflict = rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking End Date conflict", e);
        }

        return conflict;
    }

    private Project mapRowToProject(ResultSet rs) throws SQLException {
        Project project = new Project();
        project.setProjectId(rs.getInt("projectId"));
        project.setProjectName(rs.getString("projectName"));
        project.setDescription(rs.getString("description"));
        project.setProjectOwnerId(rs.getInt("projectOwnerId"));
        project.setProjectOwnerName(rs.getString("projectOwnerName"));
        project.setStartDate(rs.getString("startDate"));
        project.setEndDate(rs.getString("endDate"));
        project.setIsActive(rs.getBoolean("isActive"));
        return project;
    }

}
