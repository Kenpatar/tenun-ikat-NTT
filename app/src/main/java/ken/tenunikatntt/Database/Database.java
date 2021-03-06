package ken.tenunikatntt.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

import ken.tenunikatntt.Model.Favorites;
import ken.tenunikatntt.Model.Order;

/**
 * Created by Emilken18 on 6/18/2018.
 */

public class Database extends SQLiteAssetHelper{

    private static final String DB_NAME ="tenun-ikat-ntt-18.db";
    private static final int DB_VER =2;
    public Database(Context context){
        super(context, DB_NAME,null, DB_VER);
    }

    public boolean checkKainExists (String kainId, String userPhone)
    {
        boolean flag = false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * From OrderDetail WHERE UserPhone = '%s' AND ProductId = '%s'", userPhone, kainId);
        cursor = db.rawQuery(SQLQuery, null);
        if (cursor.getCount()>0)
            flag = true;
        else
            flag = false;
        cursor.close();
        return flag;
    }

    public List<Order> getCarts(String userPhone)
    {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"UserPhone","ProductName","ProductId","Quantity","Price","Discount","Image"};
        String sqlTable = "OrderDetail";

        qb.setTables(sqlTable);
        Cursor c = qb.query(db,sqlSelect,"UserPhone=?",new String[]{userPhone},null,null,null);

        final List<Order> result = new ArrayList<>();
        if (c.moveToFirst())
        {
            do {
                result.add(new Order(
                        c.getString(c.getColumnIndex("UserPhone")),
                        c.getString(c.getColumnIndex("ProductId")),
                        c.getString(c.getColumnIndex("ProductName")),
                        c.getString(c.getColumnIndex("Quantity")),
                        c.getString(c.getColumnIndex("Price")),
                        c.getString(c.getColumnIndex("Discount")),
                        c.getString(c.getColumnIndex("Image"))
                ));
            }while (c.moveToNext());
        }
        return result;
    }

    public void addToCart (Order order)
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT OR REPLACE INTO OrderDetail(UserPhone,ProductId,ProductName,Quantity,Price,Discount,Image)VALUES('%s','%s','%s','%s','%s','%s','%s');",
                order.getUserPhone(),
                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount(),
                order.getImage());
        db.execSQL(query);
    }

    public void cleanCart(String userPhone)
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail WHERE UserPhone='%s'",userPhone);
        db.execSQL(query);
    }

    public int getCountCart(String userPhone) {
        int count=0;
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT COUNT(*) FROM OrderDetail Where UserPhone='%s'",userPhone);
        Cursor cursor = db.rawQuery(query,null);
        if (cursor.moveToFirst())
        {
            do {
                count = cursor.getInt(0);
            }while (cursor.moveToNext());
        }
        return count;
    }

    public void updateCart(Order order) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE OrderDetail SET Quantity= '%s' WHERE UserPhone = '%s' AND ProductId = '%s'",order.getQuantity(),order.getUserPhone(),order.getProductId());
        db.execSQL(query);
    }

    public void increaseCart (String userPhone, String kainId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE OrderDetail SET Quantity= Quantity+1 WHERE UserPhone = '%s' AND ProductId = '%s'",userPhone,kainId);
        db.execSQL(query);
    }

    public void removeFromCart(String productId, String phone) {

        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail WHERE UserPhone='%s' and ProductId = '%s'",phone,productId);
        db.execSQL(query);
    }

    //Favorites
    public void addToFavorites(Favorites kain)
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO Favorites (" +
                        "KainId, KainName, KainPrice, KainMenuId, KainImage, KainDiscount, KainDescription, UserPhone) " +
                "VALUES('%s','%s','%s','%s','%s','%s','%s','%s');",
                kain.getKainId(),
                kain.getKainName(),
                kain.getKainPrice(),
                kain.getKainMenuId(),
                kain.getKainImage(),
                kain.getKainDiscount(),
                kain.getKainDescription(),
                kain.getUserPhone());
        db.execSQL(query);
    }

    public void removeFromFavorites(String KainId, String userPhone)
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM Favorites WHERE KainId ='%s' and UserPhone = '%s';",KainId, userPhone);
        db.execSQL(query);
    }

    public boolean isFavorite(String KainId, String userPhone)
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT * FROM Favorites WHERE KainId='%s' and UserPhone = '%s';",KainId, userPhone);
        Cursor cursor = db.rawQuery(query,null);
        if (cursor.getCount() <= 0)
        {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public List<Favorites> getAllFavorites(String userPhone)
    {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"UserPhone","KainId","KainName","KainPrice","KainMenuId","KainImage","KainDiscount","KainDescription"};
        String sqlTable = "Favorites";

        qb.setTables(sqlTable);
        Cursor c = qb.query(db,sqlSelect,"UserPhone=?",new String[]{userPhone},null,null,null);

        final List<Favorites> result = new ArrayList<>();
        if (c.moveToFirst())
        {
            do {
                result.add(new Favorites(
                        c.getString(c.getColumnIndex("UserPhone")),
                        c.getString(c.getColumnIndex("KainId")),
                        c.getString(c.getColumnIndex("KainName")),
                        c.getString(c.getColumnIndex("KainPrice")),
                        c.getString(c.getColumnIndex("KainMenuId")),
                        c.getString(c.getColumnIndex("KainImage")),
                        c.getString(c.getColumnIndex("KainDiscount")),
                        c.getString(c.getColumnIndex("KainDescription"))

                ));
            }while (c.moveToNext());
        }
        return result;
    }
}
