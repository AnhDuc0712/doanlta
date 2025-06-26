package model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Bảng đại diện cho một khoản thu hoặc chi trong ngày.
 */
@Entity(tableName = "finance_records")
public class FinanceRecord {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public long date;  // Ngày thực hiện (millis)

    public int amount; // Số tiền

    @NonNull
    public String type; // "income" hoặc "expense"

    public String category; // Nhóm sử dụng: ăn uống, giải trí, học tập...

    public String note; // Ghi chú (nếu có)
}
