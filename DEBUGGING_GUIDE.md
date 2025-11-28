# 🔍 Debugging Guide - App Not Showing Data

## **Issue**: App shows nothing & meals don't save

Let's debug step by step:

---

## **Step 1: Check if you're logged in**

The app requires authentication. Check Logcat for:
```
HomeFragment: User ID or access token is null
```

**If you see this:**
1. You need to **login first**
2. Go through: Splash → Welcome → Login
3. Enter valid credentials
4. Check Logcat for "✅ Login successful"

---

## **Step 2: Check Logcat for errors**

Open **Logcat** in Android Studio and filter by:
- `HomeFragment` - For Today's Progress issues
- `MealApprovalActivity` - For meal saving issues
- `JournalFragment` - For journal issues

**Look for:**
- ❌ Red error messages
- 🔴 HTTP error codes (401, 403, 500, etc.)
- ⚠️ "Failed to..." messages

---

## **Step 3: Verify Database Trigger**

**Did you run the SQL trigger in Supabase?**

1. Go to https://supabase.com/dashboard
2. Select your project
3. Go to **SQL Editor**
4. Run the contents of `database_trigger.sql`

**Without this trigger, daily summaries won't update!**

---

## **Step 4: Check Database Tables**

In Supabase dashboard:

### **Check `meals` table:**
```sql
SELECT * FROM meals ORDER BY created_at DESC LIMIT 10;
```
- Are meals being saved?
- Check `user_id`, `date`, `consumed_at` fields

### **Check `daily_summaries` table:**
```sql
SELECT * FROM daily_summaries ORDER BY date DESC LIMIT 10;
```
- Are summaries being created?
- Check `total_calories`, `total_protein`, etc.

---

## **Step 5: Test Meal Saving**

1. **Scan or enter a meal**
2. **Click Save**
3. **Check Logcat** for:
   ```
   MealApprovalActivity: Saving meal:
   MealApprovalActivity:   Food: Chicken
   MealApprovalActivity:   Calories: 200
   MealApprovalActivity: ✅ Meal saved successfully!
   ```

**If you see errors:**
- Check the error message
- Verify Supabase URL and API key are correct
- Ensure you're logged in

---

## **Step 6: Test Today's Progress**

1. **Go to Home tab**
2. **Pull down to refresh**
3. **Check Logcat** for:
   ```
   HomeFragment: Fetching daily summary for user: xxx, date: 2025-11-22
   HomeFragment: ✅ Daily summary loaded: 500 cal, 30g protein, 40g carbs, 20g fat
   ```

**If you see "No daily summary found":**
- The trigger might not be running
- Or no meals have been saved yet

---

## **Step 7: Common Issues**

### **Issue: "User ID or access token is null"**
**Fix**: Login to the app first

### **Issue: "Failed to save meal" with 401 error**
**Fix**: 
- Access token expired
- Logout and login again

### **Issue: "No daily summary found"**
**Fix**:
- Run the SQL trigger in Supabase
- Save a meal first
- Check if trigger is working:
  ```sql
  SELECT * FROM daily_summaries WHERE date = CURRENT_DATE;
  ```

### **Issue: App shows 0 for everything**
**Fix**:
- No meals saved yet, OR
- Daily summary not created (trigger not run)

---

## **Step 8: Manual Testing**

### **Test 1: Save a meal manually in Supabase**
```sql
INSERT INTO meals (user_id, food_name, calories, protein, carbs, fat, date, consumed_at, meal_type)
VALUES ('your-user-id', 'Test Meal', 500, 30, 40, 20, '2025-11-22', NOW(), 'Lunch');
```

Then check if `daily_summaries` updates automatically.

### **Test 2: Check if app can read data**
If manual insert works but app doesn't show it:
- Check authentication
- Check Logcat for API errors
- Verify PostgREST query syntax

---

## **Quick Checklist**

- [ ] Logged into the app
- [ ] SQL trigger run in Supabase
- [ ] At least one meal saved
- [ ] Checked Logcat for errors
- [ ] Verified `meals` table has data
- [ ] Verified `daily_summaries` table has data
- [ ] Pulled down to refresh on Home tab

---

## **Next Steps**

**Share with me:**
1. Logcat output (filter by `HomeFragment`, `MealApprovalActivity`)
2. Screenshot of what you see in the app
3. Result of this SQL query in Supabase:
   ```sql
   SELECT * FROM meals WHERE date = CURRENT_DATE;
   SELECT * FROM daily_summaries WHERE date = CURRENT_DATE;
   ```
