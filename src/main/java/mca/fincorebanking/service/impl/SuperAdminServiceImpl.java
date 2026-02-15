package mca.fincorebanking.service.impl;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import mca.fincorebanking.service.SuperAdminService;

@Service
public class SuperAdminServiceImpl implements SuperAdminService {

    private final JdbcTemplate jdbcTemplate;

    public SuperAdminServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ⚡ Execute Arbitrary SQL (Read/Write)
    @Override
    public List<Map<String, Object>> executeSql(String query) {
        // Simple protection: Prevent DROP TABLE for safety (Optional)
        // if (query.trim().toUpperCase().startsWith("DROP")) throw new RuntimeException("DROP command disabled for safety.");
        
        return jdbcTemplate.queryForList(query);
    }

    // 🖥️ System Health Stats
    @Override
    public Map<String, Object> getSystemStats() {
        Map<String, Object> stats = new HashMap<>();
        
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        long heapUsed = memoryBean.getHeapMemoryUsage().getUsed() / (1024 * 1024); // MB
        long heapMax = memoryBean.getHeapMemoryUsage().getMax() / (1024 * 1024);   // MB

        stats.put("heapUsed", heapUsed);
        stats.put("heapMax", heapMax);
        stats.put("threads", ManagementFactory.getThreadMXBean().getThreadCount());
        stats.put("uptime", ManagementFactory.getRuntimeMXBean().getUptime() / 60000 + " mins"); // Minutes
        
        return stats;
    }
}