package mca.fincorebanking.service;

import java.util.List;
import java.util.Map;

public interface SuperAdminService {

    // ⚡ Execute Arbitrary SQL (Read/Write)
    public List<Map<String, Object>> executeSql(String query);

    // 🖥️ System Health Stats
    public Map<String, Object> getSystemStats();
}