/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spectramd.focus.allocation.dao;

import com.spectramd.focus.allocation.entity.Allocation;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author raghav.mittal
 */
@Repository
public class AllocationDAOImpl implements AllocationDAO {

    @Autowired
    private final DataSource dataSource;

    public AllocationDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private static final String GET_ALL_ALLOCATIONS_QUERY = "select a.allocationId,a.assigneeId,e.name as assigneeName,a.projectId,p.projectName as projectName,a.allocationStartDate,a.allocationEndDate,a.allocatorName,a.percentageAllocation,a.isActive from allocation_staff a join employee_staff e on a.assigneeId = e.employeeId join project_staff p on a.projectId = p.projectId";

    private static final String GET_ALL__ACTIVE_ALLOCATIONS_QUERY = "select a.allocationId,a.assigneeId,e.name as assigneeName,a.projectId,p.projectName as projectName,a.allocationStartDate,a.allocationEndDate,a.allocatorName,a.percentageAllocation,a.isActive from allocation_staff a join employee_staff e on a.assigneeId = e.employeeId join project_staff p on a.projectId = p.projectId where a.isActive=1";

    private static final String GET_ALLOCATION_BY_ID = "select a.allocationId,a.assigneeId,e.name as assigneeName,a.projectId,p.projectName as projectName,a.allocationStartDate,a.allocationEndDate,a.allocatorName,a.percentageAllocation,a.isActive from allocation_staff a join employee_staff e on a.assigneeId = e.employeeId join project_staff p on a.projectId = p.projectId  where allocationId=?";

    private static final String GET_ALLOCATION_BY_EMP_NAME = "select a.allocationId,a.assigneeId,e.name as assigneeName,a.projectId,p.projectName as projectName,a.allocationStartDate,a.allocationEndDate,a.allocatorName,a.percentageAllocation,a.isActive FROM allocation_staff a JOIN employee_staff e ON a.assigneeId=e.employeeId  JOIN project_staff p ON a.projectId = p.projectId WHERE e.name like ?";

    private static final String GET_ONE_ALLOCATION_BY_PROJ_NAME = "select a.allocationId,a.assigneeId,e.name as assigneeName,a.projectId,p.projectName as projectName,a.allocationStartDate,a.allocationEndDate,a.allocatorName,a.percentageAllocation,a.isActive FROM allocation_staff a JOIN employee_staff e ON a.assigneeId=e.employeeId  JOIN project_staff p ON a.projectId = p.projectId WHERE p.projectName=?";

    private static final String GET_ALLOCATION_BY_PROJ_NAME = "select a.allocationId,a.assigneeId,e.name as assigneeName,a.projectId,p.projectName as projectName,a.allocationStartDate,a.allocationEndDate,a.allocatorName,a.percentageAllocation,a.isActive FROM allocation_staff a JOIN employee_staff e ON a.assigneeId = e.employeeId JOIN project_staff p ON a.projectId = p.projectId WHERE p.projectName like ?";

    private static final String GET_ALLOCATION_BY_EMPLOYEE_ID = "select a.allocationId,a.assigneeId,e.name as assigneeName,a.projectId,p.projectName as projectName,a.allocationStartDate,a.allocationEndDate,a.allocatorName,a.percentageAllocation,a.isActive from allocation_staff a JOIN employee_staff e ON a.assigneeId=e.employeeId join project_staff p ON a.projectId=p.projectId WHERE a.assigneeId=?";

    private static final String GET_ALLOCATION_BY_PROJECT_ID = "select a.allocationId,a.assigneeId,e.name as assigneeName,a.projectId,p.projectName as projectName,a.allocationStartDate,a.allocationEndDate,a.allocatorName,a.percentageAllocation,a.isActive from allocation_staff a JOIN employee_staff e ON a.assigneeId=e.employeeId join project_staff p ON a.projectId=p.projectId WHERE a.projectId=?";

    private static final String GET_EMP_ID_ACTIVE_ALLOCATIONS = "select a.allocationId,a.assigneeId,e.name as assigneeName,a.projectId,p.projectName as projectName,a.allocationStartDate,a.allocationEndDate,a.allocatorName,a.percentageAllocation,a.isActive from allocation_staff a JOIN employee_staff e ON a.assigneeId=e.employeeId join project_staff p ON a.projectId=p.projectId WHERE a.assigneeId=? AND a.isActive=1";

    private static final String GET_ALLOCATED_EMPLOYEE_NAMES = "select e.name from employee_staff e JOIN allocation_staff a ON e.employeeId = a.assigneeId where e.name like ?";

    private static final String GET_ALLOCATED_PROJECT_NAMES = "select p.projectName FROM employee_staff e JOIN allocation_staff  a ON e.employeeId = a.assigneeId  JOIN project_staff p ON a.projectId = p.projectId WHERE e.name like ?";

    private static final String GET_ALLOCATED_IDS = "select a.allocationId FROM employee_staff e JOIN allocation_staff  a ON e.employeeId = a.assigneeId WHERE e.name like ?";

        private static final String GET_ALLOCATED_EMPLOYEE_IDS = "select e.employeeId from employee_staff e join allocation_staff a on e.employeeId = a.assigneeId where e.name like ?";

    private static final String GET_ALLOC_FOR_DELETE_BY_EMP_NAME = "select a.allocationId,a.assigneeId,e.name as assigneeName,a.projectId,p.projectName as projectName,a.allocationStartDate,a.allocationEndDate,a.allocatorName,a.percentageAllocation,a.isActive FROM allocation_staff a JOIN employee_staff e ON a.assigneeId=e.employeeId  JOIN project_staff p ON a.projectId = p.projectId WHERE e.name=? AND a.isActive=1";

    private static final String GET_ALLOC_FOR_DELETE_BY_PROJ_NAME = "select a.allocationId,a.assigneeId,e.name as assigneeName,a.projectId,p.projectName as projectName,a.allocationStartDate,a.allocationEndDate,a.allocatorName,a.percentageAllocation,a.isActive FROM allocation_staff a JOIN employee_staff e ON a.assigneeId=e.employeeId  JOIN project_staff p ON a.projectId = p.projectId WHERE p.projectName=?  AND a.isActive=1";

    private static final String GET_ASSIGNEE_ID = "select employeeId from employee_staff where name=?";

    private static final String GET_PROJECT_ID = "select projectId from project_staff where projectName=?";

    private static final String GET_PROJ_ID_BY_EMP_ID = "select projectId from allocation_staff where assigneeId=?";

    private static final String ADD_ALLOCATION = "insert into allocation_staff(assigneeId,projectId,allocationStartDate,allocationEndDate,allocatorName,percentageAllocation,isActive) values(?,?,?,?,?,?,?)";

    private static final String UPDATE_ALLOCATION = "update allocation_staff set allocationStartDate=?,allocationEndDate=?,isActive=? where allocationId=?";

    private static final String UPDATE_ALLOC_BY_PROJ_ID = "update allocation_staff set allocationEndDate=?,isActive=? where projectId=?";

    private static final String DELETE_ALL_ALLOCATIONS = "update allocation_staff set isActive=0";

    private static final String DELETE_ALLOCATION_BY_ID = "update allocation_staff set isActive=0 where allocationId=?";

    private static final String DELETE_ALLOCATION_BY_EMPLOYEE_ID = "update allocation_staff set isActive=0 where assigneeId=?";

    private static final String DELETE_ALLOCATION_BY_PROJECT_ID = "update allocation_staff set isActive=0 where projectId=?";

    private static final String DELETE_ALL_BY_EMP_NAME = "update allcoation_staff set isActive=0 from allocation_staff a join employee_staff e on a.assigneeId=e.employeeId where e.name=?";

    private static final String DELETE_ALL_BY_PROJ_NAME = "update allcoation_staff set isActive=0 from allocation_staff a join project_staff p on a.projectId=p.projectId where p.projectName=?";

    private static final String ALLOCATION_ID_EXISTS = "select count(*) from allocation_staff where allocationId=?";

    private static final String ALLOCATION_EMP_ID_EXISTS = "select isActive from allocation_staff where assigneeId=?";

    private static final String ALLOCATION_PROJECT_ID_EXISTS = "select isActive from allocation_staff where projectId=?";

    private static final String ALLOCATION_EMP_JOINING_DATE_CHECK = "select case when ? >=e.dateOfJoining then cast(1 as BIT) ELSE cast(0 as BIT) end as isValid FROM employee_staff e where e.employeeId=?";

    private static final String ALLOCATION_PROJ_DATES_CHECK = "select case when ?>=p.startDate and ?<=p.endDate then cast(1 as BIT) ELSE cast(0 as BIT) end as validProjAlloc FROM project_staff p where p.projectId=?";

    private static final String SAME_PROJECT_ALLOCATION_CHECK = "select count(*) from allocation_staff where assigneeId=? and projectId=? and isActive=1";

    private static final String EMP_ACTIVE_CHECK = "select isActive from employee_staff where employeeId=?";

    private static final String PROJ_ACTIVE_CHECK = "select isActive from project_staff where projectId=?";

    @Override
    public List<Allocation> getAllAllocations() {
        List<Allocation> allocations = new ArrayList<>();

        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_ALL_ALLOCATIONS_QUERY); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                allocations.add(mapRowToAllocation(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving allocations", e);
        }
        return allocations;
    }

    /*
    @Override
    public List<Allocation> getAllAllocations() {
        return jdbcTemplate.query(GET_ALL_ALLOCATIONS_QUERY, new AllocationRowMapper());
    }
     */
    @Override
    public List<Allocation> getAllActiveAllocations() {
        List<Allocation> allocations = new ArrayList<>();

        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_ALL__ACTIVE_ALLOCATIONS_QUERY); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                allocations.add(mapRowToAllocation(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving active allocations", e);
        }
        return allocations;
    }

    @Override
    public Allocation getByAllocationId(int id) {
        Allocation allocation = null;

        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_ALLOCATION_BY_ID)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    allocation = mapRowToAllocation(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving allocation by ID", e);
        }

        return allocation;
    }

    /*
    @Override
    public Allocation getByAllocationId(int id) {
        List<Allocation> allocations = jdbcTemplate.query(GET_ALLOCATION_BY_ID, new AllocationRowMapper(), id);
        return allocations.isEmpty() ? null : allocations.get(0);
    }
     */
    @Override
    public List<Allocation> getAllAllocationByEmployeeName(String name) {

        List<Allocation> allocations = new ArrayList<>();
        String pattern = "%" + name + "%";

        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_ALLOCATION_BY_EMP_NAME)) {

            ps.setString(1, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    allocations.add(mapRowToAllocation(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving allocations by employee name", e);
        }

        return allocations;
    }

    @Override
    public Allocation getAllocationByEmployeeName(String name) {
        Allocation allocation = null;
        String pattern = "%" + name + "%";

        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_ALLOCATION_BY_EMP_NAME)) {

            ps.setString(1, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    allocation = mapRowToAllocation(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving allocation by Employee Name", e);
        }

        return allocation;
    }

    @Override
    public Allocation getSingleAllocationByProjectName(String name) {
        Allocation allocation = null;

        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_ONE_ALLOCATION_BY_PROJ_NAME)) {

            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    allocation = mapRowToAllocation(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving allocation by Project Name", e);
        }

        return allocation;
    }

    @Override
    public List<Allocation> getAllocationByProjectName(String name) {

        List<Allocation> allocations = new ArrayList<>();
        String pattern = "%" + name + "%";

        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_ALLOCATION_BY_PROJ_NAME)) {

            ps.setString(1, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    allocations.add(mapRowToAllocation(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving allocations by project name", e);
        }

        return allocations;
    }

//        @Override
//    public List<Allocation> getAllActiveAllocations() {
//        List<Allocation> allocations = new ArrayList<>();
//
//        try (
//                Connection connection = dataSource.getConnection(); 
//                PreparedStatement ps = connection.prepareStatement(GET_ALL__ACTIVE_ALLOCATIONS_QUERY); 
//                
//                ResultSet rs = ps.executeQuery()) {
//            while (rs.next()) {
//                allocations.add(mapRowToAllocation(rs));
//            }
//
//        } catch (SQLException e) {
//            throw new RuntimeException("Error retrieving active allocations", e);
//        }
//        return allocations;
//    }
    /*
    @Override
    public Allocation getAllocationByEmployeeName(String name) {
        String pattern = "%" + name + "%";
        List<Allocation> allocations = jdbcTemplate.query(GET_ALLOCATION_BY_EMP_NAME, new AllocationRowMapper(), pattern);
        return allocations.isEmpty() ? null : allocations.get(0);
    }
     */
    @Override
    public List<Allocation> getByEmployeeId(int id) {
        List<Allocation> allocations = new ArrayList<>();

        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_ALLOCATION_BY_EMPLOYEE_ID)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    allocations.add(mapRowToAllocation(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching allocations by project ID", e);
        }

        return allocations.isEmpty() ? null : allocations;
    }

    /*
    @Override
    public List<Allocation> getByEmployeeId(int id) {
        List<Allocation> allocations = jdbcTemplate.query(GET_ALLOCATION_BY_EMPLOYEE_ID, new AllocationRowMapper(), id);
        return allocations.isEmpty() ? null : allocations;
    }
     */
    @Override
    public List<Allocation> getByProjectId(int id) {
        List<Allocation> allocations = new ArrayList<>();

        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_ALLOCATION_BY_PROJECT_ID)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    allocations.add(mapRowToAllocation(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching allocations by employee ID", e);
        }

        return allocations.isEmpty() ? null : allocations;
    }

    /*
    @Override
    public List<Allocation> getByProjectId(int id) {
        List<Allocation> allocations = jdbcTemplate.query(GET_ALLOCATION_BY_PROJECT_ID, new AllocationRowMapper(), id);
        return allocations.isEmpty() ? null : allocations;
    }
     */
    @Override
    public List<Allocation> getAssigneeIdActiveAllocation(int id) {

        List<Allocation> allocations = new ArrayList<>();

        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_EMP_ID_ACTIVE_ALLOCATIONS)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    allocations.add(mapRowToAllocation(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching active allocations by employee ID", e);
        }

        return allocations;
    }

    @Override
    public List<String> getAllocatedEmployeeNames(String name) {

        List<String> names = new ArrayList<>();
        String pattern = "%" + name + "%";
        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_ALLOCATED_EMPLOYEE_NAMES)) {
            ps.setString(1, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    names.add(rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving employee names", e);
        }

        return names;
    }

    @Override
    public List<String> getAllocatedProjectNames(String name) {

        List<String> names = new ArrayList<>();
        String pattern = "%" + name + "%";

        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_ALLOCATED_PROJECT_NAMES)) {

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
    public List<Integer> getAllocatedIds(String name) {
        List<Integer> ids = new ArrayList<>();
        String pattern = "%" + name + "%";

        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_ALLOCATED_IDS)) {

            ps.setString(1, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("allocationId"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving allcoation IDs", e);
        }

        return ids;
    }

    @Override
    public List<Integer> getAllocatedEmployeeIds(String name) {

        List<Integer> ids = new ArrayList<>();
        String pattern = "%" + name + "%";

        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_ALLOCATED_EMPLOYEE_IDS)) {

            ps.setString(1, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("employeeId"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving employee ids", e);
        }

        return ids;
    }

    @Override
    public List<Allocation> getAllocForDeleteByEmpName(String name) {

        List<Allocation> allocations = new ArrayList<>();

        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_ALLOC_FOR_DELETE_BY_EMP_NAME)) {

            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    allocations.add(mapRowToAllocation(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving allocations", e);
        }

        return allocations;
    }

    @Override
    public List<Allocation> getAllocForDeleteByProjName(String name) {

        List<Allocation> allocations = new ArrayList<>();

        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_ALLOC_FOR_DELETE_BY_PROJ_NAME)) {

            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    allocations.add(mapRowToAllocation(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving allocations", e);
        }

        return allocations;
    }

    @Override
    public int getEmpIdByName(String name) {
        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_ASSIGNEE_ID)) {

            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("employeeId");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching employee ID", e);
        }
        return -1;
    }

    @Override
    public int getProjIdByName(String name) {
        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_PROJECT_ID)) {

            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("projectId");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching proejct ID", e);
        }
        return -1;
    }

    @Override
    public int getProjIdByEmpId(int id) {
        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_PROJ_ID_BY_EMP_ID)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("projectId");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching project ID by employee ID", e);
        }
        return -1;
    }

    @Override
    public int addAllocation(Allocation allocation) {
        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(ADD_ALLOCATION)) {

            ps.setInt(1, allocation.getAssigneeId());
            ps.setInt(2, allocation.getProjectId());
            ps.setString(3, allocation.getAllocationStartDate());
            ps.setString(4, allocation.getAllocationEndDate());
            ps.setString(5, allocation.getAllocatorName());
            ps.setInt(6, allocation.getPercentageAllocation());
            ps.setBoolean(7, allocation.getIsActive());

            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error adding allocation", e);
        }
    }

    /*
    @Override
    public int addAllocation(Allocation allocation) {
        return jdbcTemplate.update(ADD_ALLOCATION, allocation.getAssigneeId(), allocation.getProjectId(), allocation.getAllocationStartDate(),
                allocation.getAllocationEndDate(), allocation.getAllocatorName(), allocation.getPercentageAllocation(), allocation.getIsActive());
    }
     */
    @Override
    public int updateAllocation(Allocation allocation) {
        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(UPDATE_ALLOCATION)) {

            ps.setString(1, allocation.getAllocationStartDate());
            ps.setString(2, allocation.getAllocationEndDate());
            ps.setBoolean(3, allocation.getIsActive());
            ps.setInt(4, allocation.getAllocationId());

            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating allocation", e);
        }
    }

    /*
    @Override
    public int updateAllocation(Allocation allocation) {
        return jdbcTemplate.update(UPDATE_ALLOCATION, allocation.getAllocationEndDate(), allocation.getIsActive(), allocation.getAllocationId());
    }
     */
    @Override
    public int updateAllocationByProjId(Allocation allocation) {
        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(UPDATE_ALLOC_BY_PROJ_ID)) {

            ps.setString(1, allocation.getAllocationEndDate());
            ps.setBoolean(2, allocation.getIsActive());
            ps.setInt(3, allocation.getProjectId());

            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating allocation", e);
        }
    }

    @Override
    public int deleteAllAllocations() {
        int rowsAffected = 0;
        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(DELETE_ALL_ALLOCATIONS)) {

            rowsAffected = ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting all allocations", e);
        }

        return rowsAffected;
    }

    @Override
    public int deleteByAllocationId(int id) {
        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(DELETE_ALLOCATION_BY_ID)) {

            ps.setInt(1, id);
            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting allocation by ID", e);
        }
    }

    /*
    @Override
    public int deleteByAllocationId(int id) {
        return jdbcTemplate.update(DELETE_ALLOCATION_BY_ID, id);
    }
     */
    @Override
    public int deleteByEmployeeId(int id) {
        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(DELETE_ALLOCATION_BY_EMPLOYEE_ID)) {

            ps.setInt(1, id);
            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting allocation by employee ID", e);
        }
    }

    /*
    @Override
    public int deleteByEmployeeId(int id) {
        return jdbcTemplate.update(DELETE_ALLOCATION_BY_EMPLOYEE_ID, id);
    }
     */
    @Override
    public int deleteByProjectId(int id) {
        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(DELETE_ALLOCATION_BY_PROJECT_ID)) {

            ps.setInt(1, id);
            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting allocation by project ID", e);
        }
    }

    /*
    @Override
    public int deleteByProjectId(int id) {
        return jdbcTemplate.update(DELETE_ALLOCATION_BY_PROJECT_ID, id);
    }
     */
    @Override
    public int deleteAllEmpName(String name) {
        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(DELETE_ALL_BY_EMP_NAME)) {

            ps.setString(1, name);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting all allocations by employee name");
        }
    }

    @Override
    public int deleteAllProjName(String name) {
        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(DELETE_ALL_BY_PROJ_NAME)) {

            ps.setString(1, name);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting all allocations by project name");
        }
    }

    @Override
    public boolean allocationIdExists(int id) {
        boolean exists = false;

        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(ALLOCATION_ID_EXISTS)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if allocation ID exists", e);
        }
        return exists;
    }

    /*
    @Override
    public boolean allocationIdExists(int id) {
        Integer count = jdbcTemplate.queryForObject(ALLOCATION_ID_EXISTS, Integer.class, id);
        return count != null && count > 0;
    }
     */
    @Override
    public boolean allocationAssigneeIdExists(int id) {
        boolean exists = false;

        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(ALLOCATION_EMP_ID_EXISTS)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getBoolean("isActive");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if an active allocation for given employee ID exists", e);
        }
        return exists;
    }

    @Override
    public boolean empJoiningDateCheck(String allocationStartDate, int assigneeId) {

        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(ALLOCATION_EMP_JOINING_DATE_CHECK)) {
            ps.setString(1, allocationStartDate);
            ps.setInt(2, assigneeId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("isValid") == 1;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error validating employee joining date", e);
        }
        return false;
    }

    @Override
    public boolean projectDatesCheck(String allocationStartDate, String allocationEndDate, int projectId) {

        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(ALLOCATION_PROJ_DATES_CHECK)) {
            ps.setString(1, allocationStartDate);
            ps.setString(2, allocationEndDate);
            ps.setInt(3, projectId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("validProjAlloc") == 1;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error validating project dates", e);
        }
        return false;
    }

    /*
    @Override
    public boolean allocationAssigneeIdExists(int id) {
        Integer count = jdbcTemplate.queryForObject(ALLOCATION_EMP_ID_EXISTS, Integer.class, id);
        return count != null && count > 0;
    }
     */
    @Override
    public boolean allocationProjectIdExists(int id) {
        boolean exists = false;

        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(ALLOCATION_PROJECT_ID_EXISTS)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if allocation for given employee ID exists", e);
        }
        return exists;
    }

    /*
    @Override
    public boolean allocationProjectIdExists(int id) {
        Integer count = jdbcTemplate.queryForObject(ALLOCATION_PROJECT_ID_EXISTS, Integer.class, id);
        return count != null && count > 0;
    }
     */
    @Override
    public boolean isAllocationExistsForSameProject(int assigneeId, int projectId) {
        boolean exists = false;

        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(SAME_PROJECT_ALLOCATION_CHECK)) {

            ps.setInt(1, assigneeId);
            ps.setInt(2, projectId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if active allocation is for same project or not", e);
        }

        return exists;
    }

    @Override
    public boolean employeeIsActiveCheck(int assigneeId) {
        boolean active = false;

        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(EMP_ACTIVE_CHECK)) {

            ps.setInt(1, assigneeId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    active = rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if employee is active or not", e);
        }

        return active;
    }

    @Override
    public boolean projectIsActiveCheck(int projectId) {
        boolean active = false;

        try (
                Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(PROJ_ACTIVE_CHECK)) {

            ps.setInt(1, projectId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    active = rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if project is active or not", e);
        }

        return active;
    }

    private Allocation mapRowToAllocation(ResultSet rs) throws SQLException {
        Allocation allocation = new Allocation();
        allocation.setAllocationId(rs.getInt("allocationId"));
        allocation.setAssigneeId(rs.getInt("assigneeId"));
        allocation.setAssigneeName(rs.getString("assigneeName"));
        allocation.setProjectId(rs.getInt("projectId"));
        allocation.setProjectName(rs.getString("projectName"));
        allocation.setAllocationStartDate(rs.getString("allocationStartDate"));
        allocation.setAllocationEndDate(rs.getString("allocationEndDate"));
        allocation.setAllocatorName(rs.getString("allocatorName"));
        allocation.setPercentageAllocation(rs.getInt("percentageAllocation"));
        allocation.setIsActive(rs.getBoolean("isActive"));
        return allocation;
    }

}
