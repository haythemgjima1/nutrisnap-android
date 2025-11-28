-- ============================================
-- STEP 1: Run this in Supabase SQL Editor
-- ============================================

-- Function to update daily summary when a meal is inserted/updated/deleted
CREATE OR REPLACE FUNCTION update_daily_summary()
RETURNS TRIGGER AS $$
BEGIN
    -- For INSERT and UPDATE, use NEW values
    IF (TG_OP = 'INSERT' OR TG_OP = 'UPDATE') THEN
        INSERT INTO daily_summaries (user_id, date, total_calories, total_protein, total_carbs, total_fat, meal_count)
        SELECT 
            NEW.user_id,
            NEW.date,
            COALESCE(SUM(calories), 0),
            COALESCE(SUM(protein), 0),
            COALESCE(SUM(carbs), 0),
            COALESCE(SUM(fat), 0),
            COUNT(*)
        FROM meals
        WHERE user_id = NEW.user_id AND date = NEW.date
        ON CONFLICT (user_id, date) 
        DO UPDATE SET
            total_calories = EXCLUDED.total_calories,
            total_protein = EXCLUDED.total_protein,
            total_carbs = EXCLUDED.total_carbs,
            total_fat = EXCLUDED.total_fat,
            meal_count = EXCLUDED.meal_count,
            updated_at = NOW();
    END IF;

    -- For DELETE, use OLD values
    IF (TG_OP = 'DELETE') THEN
        INSERT INTO daily_summaries (user_id, date, total_calories, total_protein, total_carbs, total_fat, meal_count)
        SELECT 
            OLD.user_id,
            OLD.date,
            COALESCE(SUM(calories), 0),
            COALESCE(SUM(protein), 0),
            COALESCE(SUM(carbs), 0),
            COALESCE(SUM(fat), 0),
            COUNT(*)
        FROM meals
        WHERE user_id = OLD.user_id AND date = OLD.date
        ON CONFLICT (user_id, date) 
        DO UPDATE SET
            total_calories = EXCLUDED.total_calories,
            total_protein = EXCLUDED.total_protein,
            total_carbs = EXCLUDED.total_carbs,
            total_fat = EXCLUDED.total_fat,
            meal_count = EXCLUDED.meal_count,
            updated_at = NOW();
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger
DROP TRIGGER IF EXISTS meals_daily_summary_trigger ON meals;
CREATE TRIGGER meals_daily_summary_trigger
    AFTER INSERT OR UPDATE OR DELETE ON meals
    FOR EACH ROW
    EXECUTE FUNCTION update_daily_summary();

-- ============================================
-- STEP 2: Manually recalculate existing data
-- ============================================

-- This will calculate summaries for all existing meals
INSERT INTO daily_summaries (user_id, date, total_calories, total_protein, total_carbs, total_fat, meal_count)
SELECT 
    user_id,
    date,
    COALESCE(SUM(calories), 0) as total_calories,
    COALESCE(SUM(protein), 0) as total_protein,
    COALESCE(SUM(carbs), 0) as total_carbs,
    COALESCE(SUM(fat), 0) as total_fat,
    COUNT(*) as meal_count
FROM meals
GROUP BY user_id, date
ON CONFLICT (user_id, date) 
DO UPDATE SET
    total_calories = EXCLUDED.total_calories,
    total_protein = EXCLUDED.total_protein,
    total_carbs = EXCLUDED.total_carbs,
    total_fat = EXCLUDED.total_fat,
    meal_count = EXCLUDED.meal_count,
    updated_at = NOW();

-- ============================================
-- STEP 3: Verify it worked
-- ============================================

-- Check your daily summaries
SELECT * FROM daily_summaries ORDER BY date DESC LIMIT 10;

-- Check today's summary specifically
SELECT * FROM daily_summaries 
WHERE date = CURRENT_DATE 
ORDER BY updated_at DESC;
