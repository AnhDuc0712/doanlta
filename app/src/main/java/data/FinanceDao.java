package data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import model.FinanceRecord;
import data.CategoryTotal;

@Dao
public interface FinanceDao {

    // Thêm khoản thu/chi
    @Insert
    void insert(FinanceRecord record);

    // Lấy danh sách khoản thu/chi theo khoảng thời gian
    @Query("SELECT * FROM finance_records WHERE date BETWEEN :start AND :end")
    List<FinanceRecord> getRecordsByDateRange(long start, long end);

    // Tổng thu theo khoảng thời gian
    @Query("SELECT SUM(amount) FROM finance_records WHERE type = 'income' AND date BETWEEN :start AND :end")
    int getTotalIncome(long start, long end);

    // Tổng chi theo khoảng thời gian
    @Query("SELECT SUM(amount) FROM finance_records WHERE type = 'expense' AND date BETWEEN :start AND :end")
    int getTotalExpense(long start, long end);

    // Gom nhóm theo loại chi tiêu: category
    @Query("SELECT category, SUM(amount) as total FROM finance_records WHERE type = 'expense' AND date BETWEEN :start AND :end GROUP BY category")
    List<CategoryTotal> getExpenseByCategory(long start, long end);
}
