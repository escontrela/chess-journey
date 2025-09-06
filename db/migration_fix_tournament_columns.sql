-- Migration script to fix tournament column mapping bug
-- This script updates the tournaments table structure to correctly handle local vs ritmo data

-- 1. Add the new 'local' column
ALTER TABLE public.tournaments ADD COLUMN IF NOT EXISTS local VARCHAR(255);

-- 2. Make ritmo column nullable since it may not be available from HTML table
ALTER TABLE public.tournaments ALTER COLUMN ritmo DROP NOT NULL;

-- 3. Move existing 'ritmo' data to 'local' column (since it was incorrectly stored)
-- This assumes that the current 'ritmo' column actually contains location data
UPDATE public.tournaments 
SET local = ritmo 
WHERE local IS NULL AND ritmo IS NOT NULL;

-- 4. Clear the ritmo column since it contained incorrect data
UPDATE public.tournaments SET ritmo = NULL;

-- 5. Optional: Add comments to document the columns
COMMENT ON COLUMN public.tournaments.local IS 'Location/venue where the tournament is held';
COMMENT ON COLUMN public.tournaments.ritmo IS 'Optional: Time control/rhythm (may not be available from website)';