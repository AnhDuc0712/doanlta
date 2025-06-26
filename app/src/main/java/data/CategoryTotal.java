package data;

/**
 * Lớp này đại diện cho kết quả thống kê:
 * Tổng chi theo từng nhóm chi tiêu (category)
 * - Dùng trong truy vấn GROUP BY category
 */
public class CategoryTotal {
    public String category;
    public int total;
}
