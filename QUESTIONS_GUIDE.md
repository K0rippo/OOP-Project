# Question Management System - CSV Format

## Overview
Questions are now loaded from a CSV file (`assets/questions.csv`) instead of being hardcoded. This allows you to manage questions without recompiling the code.

## CSV File Location
- **File**: `C:\Users\elvan\git\OOP-Project\assets\questions.csv`
- **Backup copies**: 
  - `lwjgl3/bin/main/questions.csv`
  - `core/bin/main/questions.csv`

## CSV Format

### Header Row
```
question,correct_answer,wrong_answer_1,wrong_answer_2,theme_color_r,theme_color_g,theme_color_b,time_limit
```

### Field Descriptions

| Field | Type | Description | Example |
|-------|------|-------------|---------|
| `question` | String | The question text to display | "What is 2 + 2?" |
| `correct_answer` | String | The correct answer (MUST be first) | "4" |
| `wrong_answer_1` | String | First incorrect option | "3" |
| `wrong_answer_2` | String | Second incorrect option | "5" |
| `theme_color_r` | Float | Red component (0.0-1.0) | "0.2" |
| `theme_color_g` | Float | Green component (0.0-1.0) | "0.1" |
| `theme_color_b` | Float | Blue component (0.0-1.0) | "0.15" |
| `time_limit` | Float | Time to reach wall in seconds | "5" |

## Example CSV Entry
```csv
What is the capital of France?,Paris,London,Berlin,0.25,0.15,0.15,5
```

This creates a question:
- **Text**: "What is the capital of France?"
- **Correct Answer**: Paris
- **Wrong Answers**: London, Berlin
- **Theme Color**: RGB(0.25, 0.15, 0.15) = Reddish brown
- **Time Limit**: 5 seconds

## Color Reference

### Common Color Values (RGB)
```
Black:      0.0, 0.0, 0.0
White:      1.0, 1.0, 1.0
Red:        1.0, 0.0, 0.0
Green:      0.0, 1.0, 0.0
Blue:       0.0, 0.0, 1.0
Yellow:     1.0, 1.0, 0.0
Cyan:       0.0, 1.0, 1.0
Magenta:    1.0, 0.0, 1.0
Orange:     1.0, 0.5, 0.0
Purple:     0.5, 0.0, 0.5
```

## How It Works

### Loading Process
1. Game starts → `GameMaster.create()` is called
2. `CsvQuestionProvider` is instantiated
3. Reads `questions.csv` from assets
4. Parses each line into a Question object
5. Questions are shuffled for variety
6. If CSV fails, falls back to hardcoded default questions

### Fallback Behavior
If `questions.csv` is not found or cannot be parsed:
- Console prints warning: `⚠️ questions.csv not found! Using default questions.`
- Game uses 5 built-in questions as backup
- Game continues normally

## Adding New Questions

### Step 1: Edit questions.csv
Open `C:\Users\elvan\git\OOP-Project\assets\questions.csv` and add a new line:

```csv
What is the largest planet in our solar system?,Jupiter,Saturn,Neptune,0.15,0.25,0.15,5
```

### Step 2: Update Backup Copies
Copy the updated file to bin folders (for Eclipse running):
```bash
copy assets\questions.csv lwjgl3\bin\main\questions.csv
copy assets\questions.csv core\bin\main\questions.csv
```

### Step 3: Rebuild Project
```bash
gradlew clean build
```

### Step 4: Run Game
Click Play - the new questions will be loaded from the CSV!

## Editing Tips

### Important Rules
1. **Correct answer MUST be first** (index 0 in the CSV)
2. **Commas in question text** must be inside quotes: `"What is X, Y, or Z?"`
3. **No quotes in simple answers** - keep them simple
4. **Color values** must be between 0.0 and 1.0
5. **Time limit** should be realistic (3-6 seconds recommended)

### Special Cases

#### Question with comma:
```csv
"What is X, Y, or Z?",X,Y,Z,0.1,0.2,0.3,5
```

#### Question with quotes:
```csv
Who said ""To be or not to be""?,Shakespeare,Marlowe,Bacon,0.2,0.1,0.2,6
```

## Performance Notes
- Questions are shuffled on each game start for variety
- CSV is parsed once at startup
- No performance impact during gameplay
- Large CSV files (100+ questions) are fine

## Troubleshooting

### Questions not loading?
1. Check console for error messages
2. Verify `questions.csv` exists in `assets/` folder
3. Check CSV format matches specification
4. Ensure no extra spaces around commas
5. Rebuild project: `gradlew clean build`

### Seeing default questions instead of CSV?
1. Verify `questions.csv` is in correct location
2. Copy file to bin folders
3. Check console logs for parsing errors
4. Rebuild and restart game

### CSV editing issues?
Use a proper text editor (Notepad++, VS Code) not Word:
- Word may corrupt formatting
- Ensure UTF-8 encoding
- Save as `.csv` not `.txt`

## Summary
You now have complete control over questions! Edit `questions.csv`, rebuild, and play with your new questions. The CSV approach makes question management much easier while keeping your code clean and following the Open/Closed Principle (open for extension via data, closed for modification of code).
