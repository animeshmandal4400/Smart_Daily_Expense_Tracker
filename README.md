# Smart Daily Expense Tracker

### App Overview
Smart Daily Expense Tracker is a multi-screen expense module built with Jetpack Compose, Room, and Hilt using a clean MVVM architecture. It lets users add expenses quickly, browse and filter past records, and view a simple weekly report with category breakdowns.
The app includes three screens: Expense Entry, Expense List with grouping/filtering, and a mock Reports view with export/share simulations.

### AI Usage Summary
- I used **Figma AI** to quickly create initial UI designs, choose color palettes, and get layout inspiration before starting development.
- **Cursor** helped me set up and refine the MVVM architecture, manage safe code refactors, and make quick edits across UI components, ViewModels, DAOs, and navigation.
- **ChatGPT** was my go-to for reviewing requirements, and helping me write/iterate on Compose UI, Room queries, and ViewModel logic for validation, grouping, date handling, and export simulations. It also provided mock data for testing and improving the app during development.
- AI helped me polish the project by pointing out improvements like aligning category constants, unifying currency formats, and handling “today” based on local time. It even assisted in drafting this README.

### Prompt Logs (key prompts)
- Design a modern mobile app UI in Material You style for a “Smart Daily Expense Tracker” targeted at small business owners. The app should have three main screens: 1. Expense Entry Screen: Header: “Total Spent Today: ₹XXXX” (real-time indicator) Form fields: Title (text field, required) Amount (numeric field, required, ₹ prefix) Category (dropdown: Staff, Travel, Food, Utility) Notes (max 100 chars, small text field) Receipt Image (upload button or image preview) Submit button: Rounded, primary color, with an add icon Smooth entry animation when an expense is added Light & dark theme previews 2. Expense List Screen: Default view: Today’s expenses in a scrollable list Filters: Date picker for previous days Group toggle: “By Category” / “By Time” Each expense card: Category icon, title, amount, date/time Top bar: Total expenses count + total amount Empty state illustration for no data 3. Expense Report Screen: Title: “Weekly Expense Report” Show daily totals in a bar or line chart (7 days) Category-wise totals Export button: PDF/CSV share button General Style: Clean, minimalist, and intuitive Use a soft color palette with clear category color-coding Consistent rounded corners (8–12px) Icons for categories (Staff, Travel, Food, Utility) Apply Material Design typography hierarchy Include both light mode and dark mode Output: Mobile screen mockups in portrait mode Show all three screens side-by-side Provide clickable prototype flow from entry → list → report"
- Compose a form to add an expense with fields: Title, Amount (₹ prefix), Category (Staff, Travel, Food, Utility), Notes (<=100 chars), and a mocked “Upload Receipt” button. Include validation states and a primary submit button with a subtle press animation.
- Set up a bottom bar NavHost with three destinations: Entry, List, Report. Use route constants, restore state when switching tabs, and make Entry the start destination. Provide a concise example with `NavigationBarItem`s.

### Checklist of Features Implemented
- Expense Entry Screen
  - [x] Title input
  - [x] Amount input with ₹ prefix
  - [x] Category selection (mocked list)
  - [x] Optional Notes (max 100 chars)
  - [x] Optional Receipt Image (mocked picker dialog)
  - [x] Submit button with validation, toast, and lightweight animation
  - [x] Real-time "Total Spent Today" card at the top

- Expense List Screen
  - [x] Default view: Today
  - [x] Filter by date ranges (Today, This Week, This Month, All Time) and custom date via date picker
  - [x] Filter by category
  - [x] Grouping toggle: By Time or By Category
  - [x] Shows total count, total amount, average amount
  - [x] Empty state when no results
  
- Expense Report Screen (Mocked)
  - [x] Last 7 days summary (weekly total and average daily)
  - [x] Daily Expenses (mock chart)
  - [x] Category-wise totals (mock data)
  - [x] Export simulated (PDF/CSV) and Share simulation via snackbars

- Architecture & Platform
  - [x] MVVM with `ViewModel` + `StateFlow`
  - [x] Room (Entity/DAO/Database) with `Date` TypeConverter
  - [x] Repository abstraction
  - [x] Hilt DI for repository and database/DAO
  - [x] Jetpack Compose UI and Navigation with a bottom bar across three screens

## 📥 APK Download

You can download the latest APK for **Smart Daily Expense Tracker** here:  
[Download APK v1.0.0](https://github.com/animeshmandal4400/Smart_Daily_Expense_Tracker/releases/download/v1.0.0/expense_tracker.apk)

Install this APK on your Android device to try out the app without building it yourself.

## Screenshots
<img width="474" height="1016" alt="image" src="https://github.com/user-attachments/assets/c4a3914d-12c9-485f-8eaa-ff6fa0249b29" />
<img width="470" height="1022" alt="image" src="https://github.com/user-attachments/assets/559cfd60-f0c9-48ba-8b78-545000ce4079" />
<img width="478" height="1018" alt="image" src="https://github.com/user-attachments/assets/96374812-e2bd-4827-acac-09fea01234a5" />
<img width="476" height="1016" alt="image" src="https://github.com/user-attachments/assets/d57272a1-73fb-4efb-bcc5-0b526e19003b" />




