package com.example.data.seed

import com.example.data.local.AdSettingsEntity
import com.example.data.local.AdminUserEntity
import com.example.data.local.AppSettingsEntity
import com.example.data.local.CategoryEntity
import com.example.data.local.ExamEntity
import com.example.data.local.LevelEntity
import com.example.data.local.NotificationEntity
import com.example.data.local.QuestionEntity
import com.example.data.local.UserEntity
import java.security.MessageDigest

object SeedData {

  fun hashPassword(password: String, salt: String): String {
    val md = MessageDigest.getInstance("SHA-256")
    val bytes = md.digest((password + salt).toByteArray(Charsets.UTF_8))
    return bytes.joinToString("") { "%02x".format(it) }
  }

  val initialAdmin: AdminUserEntity = run {
    val salt = "quiz_salt_admin_2026"
    val hash = hashPassword("admin123", salt)
    AdminUserEntity(
      id = 1,
      username = "admin",
      passwordHash = hash,
      salt = salt,
      fullName = "সুপার অ্যাডমিন (Admin)",
      role = "SUPER_ADMIN"
    )
  }

  val initialUsers: List<UserEntity> = listOf(
    UserEntity(
      id = 1,
      username = "tanvir_bd",
      email = "tanvir@example.com",
      passwordHash = hashPassword("123456", "user_salt_1"),
      salt = "user_salt_1",
      fullName = "তানভীর আহমেদ",
      totalPoints = 2850,
      currentLevel = 8,
      completedQuizzes = 42,
      completedExams = 12
    ),
    UserEntity(
      id = 2,
      username = "nusrat_jahan",
      email = "nusrat@example.com",
      passwordHash = hashPassword("123456", "user_salt_2"),
      salt = "user_salt_2",
      fullName = "নুসরাত জাহান",
      totalPoints = 2640,
      currentLevel = 7,
      completedQuizzes = 38,
      completedExams = 9
    ),
    UserEntity(
      id = 3,
      username = "mehrab_bcs",
      email = "mehrab@example.com",
      passwordHash = hashPassword("123456", "user_salt_3"),
      salt = "user_salt_3",
      fullName = "মেহরাব হোসেন",
      totalPoints = 2410,
      currentLevel = 6,
      completedQuizzes = 35,
      completedExams = 11
    ),
    UserEntity(
      id = 4,
      username = "sabrina_akhtar",
      email = "sabrina@example.com",
      passwordHash = hashPassword("123456", "user_salt_4"),
      salt = "user_salt_4",
      fullName = "সাবরিনা আক্তার",
      totalPoints = 2190,
      currentLevel = 5,
      completedQuizzes = 29,
      completedExams = 7
    ),
    UserEntity(
      id = 5,
      username = "hasan_ict",
      email = "hasan@example.com",
      passwordHash = hashPassword("123456", "user_salt_5"),
      salt = "user_salt_5",
      fullName = "হাসান মাহমুদ",
      totalPoints = 1950,
      currentLevel = 5,
      completedQuizzes = 24,
      completedExams = 6
    ),
    UserEntity(
      id = 6,
      username = "demo_player",
      email = "player@quiz.bd",
      passwordHash = hashPassword("123456", "user_salt_demo"),
      salt = "user_salt_demo",
      fullName = "কুইজ শিক্ষার্থী (আপনি)",
      totalPoints = 480,
      currentLevel = 2,
      completedQuizzes = 6,
      completedExams = 2
    )
  )

  val initialCategories: List<CategoryEntity> = listOf(
    CategoryEntity(1, "বাংলাদেশ", "Bangladesh", "flag", "#0D7A4A", 25, true),
    CategoryEntity(2, "মুক্তিযুদ্ধ", "Liberation War 1971", "military_tech", "#DC2626", 20, true),
    CategoryEntity(3, "ইতিহাস", "History", "history_edu", "#854D0E", 18, true),
    CategoryEntity(4, "ভূগোল", "Geography", "public", "#0284C7", 16, true),
    CategoryEntity(5, "বিজ্ঞান", "Science", "science", "#7C3AED", 22, true),
    CategoryEntity(6, "সাধারণ জ্ঞান", "General Knowledge", "psychology", "#EA580C", 30, true),
    CategoryEntity(7, "সাহিত্য", "Literature", "menu_book", "#DB2777", 20, true),
    CategoryEntity(8, "বাংলা ভাষা", "Bangla Language", "translate", "#059669", 18, true),
    CategoryEntity(9, "ইংরেজি", "English", "spellcheck", "#2563EB", 15, true),
    CategoryEntity(10, "গণিত", "Mathematics", "calculate", "#D97706", 18, true),
    CategoryEntity(11, "ICT", "ICT & Computer", "computer", "#0891B2", 20, true),
    CategoryEntity(12, "খেলাধুলা", "Sports", "sports_soccer", "#16A34A", 16, true),
    CategoryEntity(13, "আন্তর্জাতিক", "International", "language", "#4F46E5", 22, true),
    CategoryEntity(14, "চাকরি প্রস্তুতি", "Job Preparation", "work", "#CA8A04", 25, true),
    CategoryEntity(15, "BCS প্রস্তুতি", "BCS Preparation", "school", "#B91C1C", 35, true),
    CategoryEntity(16, "ব্যাংকিং", "Banking Preparation", "account_balance", "#047857", 18, true),
    CategoryEntity(17, "প্রাথমিক শিক্ষক", "Primary Teacher", "person_search", "#9333EA", 16, true),
    CategoryEntity(18, "বিশ্ববিদ্যালয় ভর্তি", "University Admission", "auto_stories", "#E11D48", 20, true)
  )

  fun generateLevels(): List<LevelEntity> {
    return (1..100).map { lvl ->
      val req = if (lvl == 1) 0 else (lvl - 1) * 150
      val pass = 70 + (lvl % 5) * 2
      val diff = when {
        lvl <= 10 -> "EASY"
        lvl <= 35 -> "MEDIUM"
        else -> "HARD"
      }
      LevelEntity(
        levelNumber = lvl,
        titleBn = "লেভেল $lvl: ${getLevelTitle(lvl)}",
        requiredPoints = req,
        passingScore = minOf(pass, 90),
        timeLimitSeconds = if (lvl <= 20) 25 else 20,
        rewardPoints = 40 + lvl * 10,
        difficulty = diff,
        isUnlocked = lvl <= 2,
        isCompleted = lvl == 1,
        bestScore = if (lvl == 1) 90 else 0
      )
    }
  }

  private fun getLevelTitle(lvl: Int): String {
    return when (lvl) {
      1 -> "সূচনা পর্যায় (Basic Knowledge)"
      2 -> "নবীন অভিযাত্রী (Junior Explorer)"
      3 -> "বাংলাদেশ পরিচিতি (BD Explorer)"
      4 -> "মুক্তিযুদ্ধের চেতনা (Liberation Spirit)"
      5 -> "বাংলা ব্যাকরণ ও ভাষা (Grammar Master)"
      6 -> "সাধারণ বিজ্ঞানের আলো (Science Basics)"
      7 -> "আন্তর্জাতিক ঘটনাপ্রবাহ (World Affairs)"
      8 -> "গণিত ও আইসিটি বুনিয়াদি (Math & ICT)"
      9 -> "বিসিএস প্রাথমিক স্তর (BCS Level 1)"
      10 -> "মাস্টার চ্যালেঞ্জ (Master Challenge)"
      else -> "উচ্চতর মেধা যাচাই পর্যায় $lvl"
    }
  }

  val initialQuestions: List<QuestionEntity> = listOf(
    // বাংলাদেশ
    QuestionEntity(
      id = 1,
      questionBn = "বাংলাদেশের সাংবিধানিক নাম কী?",
      optionsCsv = "গণপ্রজাতন্ত্রী বাংলাদেশ||বাংলাদেশ প্রজাতন্ত্র||পিপলস রিপাবলিক অব বেঙ্গল||ইসলামী প্রজাতন্ত্র বাংলাদেশ",
      correctOptionIndex = 0,
      explanationBn = "বাংলাদেশের সংবিধানের অনুচ্ছেদ ১ অনুযায়ী রাষ্ট্রের নাম 'গণপ্রজাতন্ত্রী বাংলাদেশ' (People's Republic of Bangladesh)।",
      categoryId = 1,
      levelNumber = 1,
      difficulty = "EASY",
      points = 10
    ),
    QuestionEntity(
      id = 2,
      questionBn = "বাংলাদেশের জাতীয় সংসদের স্থপতি কে?",
      optionsCsv = "লুই আই কান||মাজহারুল ইসলাম||এফ আর খান||পল রুডলফ",
      correctOptionIndex = 0,
      explanationBn = "শেরেবাংলা নগরে অবস্থিত জাতীয় সংসদ ভবনের মূল নকশা তৈরি করেন প্রখ্যাত মার্কিন স্থপতি লুই আই কান।",
      categoryId = 1,
      levelNumber = 1,
      difficulty = "EASY",
      points = 10
    ),
    QuestionEntity(
      id = 3,
      questionBn = "পদ্মা সেতুর মোট দৈর্ঘ্য কত কিলোমিটার?",
      optionsCsv = "৬.১৫ কিমি||৫.৮০ কিমি||৬.৭৫ কিমি||৭.২০ কিমি",
      correctOptionIndex = 0,
      explanationBn = "স্বপ্নের পদ্মা সেতুর মূল দৈর্ঘ্য ৬.১৫ কিলোমিটার এবং এতে মোট ৪১টি স্প্যান রয়েছে।",
      categoryId = 1,
      levelNumber = 2,
      difficulty = "MEDIUM",
      points = 15
    ),
    QuestionEntity(
      id = 4,
      questionBn = "বাংলাদেশের একমাত্র প্রবাল দ্বীপ কোনটি?",
      optionsCsv = "সেন্টমার্টিন||নিঝুম দ্বীপ||ছেঁড়া দ্বীপ||কুতুবদিয়া",
      correctOptionIndex = 0,
      explanationBn = "কক্সবাজার জেলার টেকনাফ থেকে প্রায় ৯ কিমি দক্ষিণে অবস্থিত সেন্টমার্টিন বাংলাদেশের একমাত্র প্রবাল দ্বীপ।",
      categoryId = 1,
      levelNumber = 2,
      difficulty = "EASY",
      points = 10
    ),
    // মুক্তিযুদ্ধ
    QuestionEntity(
      id = 5,
      questionBn = "বঙ্গবন্ধু শেখ মুজিবুর রহমান ঐতিহাসিক ৭ই মার্চের ভাষণ কোথায় প্রদান করেন?",
      optionsCsv = "রেসকোর্স ময়দান (বর্তমান সোহরাওয়ার্দী উদ্যান)||পল্টন ময়দান||ধানমন্ডি ৩২ নম্বর||ঢাকা বিশ্ববিদ্যালয়",
      correctOptionIndex = 0,
      explanationBn = "১৯৭১ সালের ৭ই মার্চ তৎকালীন রেসকোর্স ময়দানে বঙ্গবন্ধু তাঁর ঐতিহাসিক ১৮ মিনিটের কালজয়ী ভাষণ দেন।",
      categoryId = 2,
      levelNumber = 1,
      difficulty = "EASY",
      points = 10
    ),
    QuestionEntity(
      id = 6,
      questionBn = "১৯৭১ সালে মুক্তিযুদ্ধের সময় বাংলাদেশকে কয়টি সেক্টরে বিভক্ত করা হয়েছিল?",
      optionsCsv = "১১টি||৮টি||৯টি||১৪টি",
      correctOptionIndex = 0,
      explanationBn = "১৯৭১ সালের জুলাই মাসের সেক্টর কমান্ডার্স কনফারেন্সে সমগ্র বাংলাদেশকে ১১টি প্রশাসনিক সেক্টরে ভাগ করা হয়।",
      categoryId = 2,
      levelNumber = 1,
      difficulty = "EASY",
      points = 10
    ),
    QuestionEntity(
      id = 7,
      questionBn = "বীরশ্রেষ্ঠ উপাধিপ্রাপ্ত মোট মুক্তিযোদ্ধার সংখ্যা কত?",
      optionsCsv = "৭ জন||৮ জন||১১ জন||৬৮ জন",
      correctOptionIndex = 0,
      explanationBn = "মুক্তিযুদ্ধে সর্বোচ্চ বীরত্ব ও আত্মত্যাগের জন্য ৭ জন সূর্যসন্তানকে 'বীরশ্রেষ্ঠ' খেতাবে ভূষিত করা হয়।",
      categoryId = 2,
      levelNumber = 2,
      difficulty = "EASY",
      points = 10
    ),
    // ইতিহাস
    QuestionEntity(
      id = 8,
      questionBn = "পলাশীর যুদ্ধ কত সালে সংঘটিত হয়েছিল?",
      optionsCsv = "১৭৫৭ সালে||১৭৬৪ সালে||১৮৫৭ সালে||১৭৭৬ সালে",
      correctOptionIndex = 0,
      explanationBn = "১৭৫৭ সালের ২৩ জুন পলাশীর আম্রকাননে নবাব সিরাজউদ্দৌলা ও রবার্ট ক্লাইভের নেতৃত্বাধীন ব্রিটিশদের মধ্যে যুদ্ধ সংঘটিত হয়।",
      categoryId = 3,
      levelNumber = 2,
      difficulty = "MEDIUM",
      points = 10
    ),
    // ভূগোল
    QuestionEntity(
      id = 9,
      questionBn = "বাংলাদেশের সর্বোচ্চ পর্বতশৃঙ্গ কোনটি?",
      optionsCsv = "সাকা হাফং (ত্লাংময়)||তাজিংডং||কেওক্রাডং||গারো পাহাড়",
      correctOptionIndex = 0,
      explanationBn = "বান্দরবান জেলায় অবস্থিত সাকা হাফং (মোদক ত্ল্যাং) এর উচ্চতা আনুমানিক ৩,৪৫১ ফুট যা বর্তমানে স্বীকৃত সর্বোচ্চ শৃঙ্গ।",
      categoryId = 4,
      levelNumber = 2,
      difficulty = "MEDIUM",
      points = 10
    ),
    // বিজ্ঞান
    QuestionEntity(
      id = 10,
      questionBn = "কোন রঙের আলোর তরঙ্গদৈর্ঘ্য সবচেয়ে বেশি?",
      optionsCsv = "লাল||নীল||বেগুনি||হলুদ",
      correctOptionIndex = 0,
      explanationBn = "দৃশ্যমান আলোর মধ্যে লাল আলোর তরঙ্গদৈর্ঘ্য সবচেয়ে বেশি এবং বেগুনি আলোর তরঙ্গদৈর্ঘ্য সবচেয়ে কম।",
      categoryId = 5,
      levelNumber = 3,
      difficulty = "MEDIUM",
      points = 15
    ),
    QuestionEntity(
      id = 11,
      questionBn = "মানুষের রক্তে হিমোগ্লোবিনের প্রধান কাজ কী?",
      optionsCsv = "অক্সিজেন পরিবহন করা||রোগ প্রতিরোধ করা||রক্ত জমাট বাঁধা||রক্তচাপ নিয়ন্ত্রণ করা",
      correctOptionIndex = 0,
      explanationBn = "লোহিত রক্তকণিকায় থাকা হিমোগ্লোবিন ফুসফুস থেকে সারা দেহে অক্সিজেন পরিবহন করে।",
      categoryId = 5,
      levelNumber = 3,
      difficulty = "EASY",
      points = 10
    ),
    // সাধারণ জ্ঞান
    QuestionEntity(
      id = 12,
      questionBn = "বিশ্ব পরিবেশ দিবস কোন তারিখে পালিত হয়?",
      optionsCsv = "৫ জুন||১ মে||৮ মার্চ||২২ এপ্রিল",
      correctOptionIndex = 0,
      explanationBn = "পরিবেশ সচেতনতা বৃদ্ধির লক্ষ্যে জাতিসংঘ প্রতি বছর ৫ই জুন বিশ্ব পরিবেশ দিবস হিসেবে পালন করে।",
      categoryId = 6,
      levelNumber = 1,
      difficulty = "EASY",
      points = 10
    ),
    // সাহিত্য ও ভাষা
    QuestionEntity(
      id = 13,
      questionBn = "বাংলা সাহিত্যের প্রাচীনতম নিদর্শন কোনটি?",
      optionsCsv = "চর্যাপদ||শ্রীকৃষ্ণকীর্তন||মঙ্গলকাব্য||পদ্মাবতী",
      correctOptionIndex = 0,
      explanationBn = "হরপ্রসাদ শাস্ত্রী ১৯০৭ সালে নেপালের রাজদরবার থেকে চর্যাপদের পুথি আবিষ্কার করেন। এটি প্রাচীন যুগের একমাত্র লিখিত নিদর্শন।",
      categoryId = 7,
      levelNumber = 2,
      difficulty = "EASY",
      points = 10
    ),
    QuestionEntity(
      id = 14,
      questionBn = "'সঞ্চিতা' কোন কবির কাব্যসংকলন?",
      optionsCsv = "কাজী নজরুল ইসলাম||রবীন্দ্রনাথ ঠাকুর||জীবনানন্দ দাশ||জসীমউদ্দীন",
      correctOptionIndex = 0,
      explanationBn = "'সঞ্চিতা' জাতীয় কবি কাজী নজরুল ইসলামের বিখ্যাত কাব্য সংকলন, অপরদিকে 'সঞ্চয়িতা' রবীন্দ্রনাথ ঠাকুরের।",
      categoryId = 7,
      levelNumber = 3,
      difficulty = "MEDIUM",
      points = 10
    ),
    QuestionEntity(
      id = 15,
      questionBn = "সন্ধি ব্যাকরণের কোন অংশে আলোচিত হয়?",
      optionsCsv = "ধ্বনি তত্ত্বে (Phonology)||শব্দ তত্ত্বে||বাক্য তত্ত্বে||অর্থ তত্ত্বে",
      correctOptionIndex = 0,
      explanationBn = "সন্ধি মূলত ধ্বনির সঙ্গে ধ্বনির মিলন বা পরিবর্তন, তাই ব্যাকরণের ধ্বনিতত্ত্বে সন্ধি বিস্তারিত আলোচনা করা হয়।",
      categoryId = 8,
      levelNumber = 3,
      difficulty = "MEDIUM",
      points = 10
    ),
    // ইংরেজি
    QuestionEntity(
      id = 16,
      questionBn = "What is the synonym of the word 'Pinnacle'?",
      optionsCsv = "Peak||Valley||Bottom||Base",
      correctOptionIndex = 0,
      explanationBn = "'Pinnacle' অর্থ চূড়া বা সর্বোচ্চ শিখর। এর প্রতিশব্দ হলো 'Peak', 'Apex' বা 'Zenith'।",
      categoryId = 9,
      levelNumber = 3,
      difficulty = "MEDIUM",
      points = 15
    ),
    // গণিত
    QuestionEntity(
      id = 17,
      questionBn = "১ থেকে ১০০ পর্যন্ত মোট কতটি মৌলিক সংখ্যা (Prime Numbers) রয়েছে?",
      optionsCsv = "২৫টি||২১টি||২৪টি||২৬টি",
      correctOptionIndex = 0,
      explanationBn = "১ থেকে ১০০ পর্যন্ত ২৫টি মৌলিক সংখ্যা রয়েছে (যেমন: ২, ৩, ৫, ৭, ১১, ১৩, ১৭, ১৯... ৯৭)।",
      categoryId = 10,
      levelNumber = 3,
      difficulty = "MEDIUM",
      points = 15
    ),
    // ICT
    QuestionEntity(
      id = 18,
      questionBn = "কম্পিউটারের মস্তিষ্ক (Brain of Computer) কাকে বলা হয়?",
      optionsCsv = "CPU||RAM||Hard Disk||Motherboard",
      correctOptionIndex = 0,
      explanationBn = "Central Processing Unit (CPU) কম্পিউটারের যাবতীয় হিসাব-নিকাশ ও নির্দেশ প্রক্রিয়াকরণ করে বিধায় একে মস্তিষ্কের সাথে তুলনা করা হয়।",
      categoryId = 11,
      levelNumber = 1,
      difficulty = "EASY",
      points = 10
    ),
    QuestionEntity(
      id = 19,
      questionBn = "HTML এর পূর্ণরূপ কী?",
      optionsCsv = "HyperText Markup Language||HighText Machine Language||Hyperlink Text Multi Language||Home Tool Markup Language",
      correctOptionIndex = 0,
      explanationBn = "HTML হলো HyperText Markup Language, যা ওয়েব পেজ তৈরির আদর্শ কাঠামোগত ভাষা।",
      categoryId = 11,
      levelNumber = 2,
      difficulty = "EASY",
      points = 10
    ),
    // খেলাধুলা
    QuestionEntity(
      id = 20,
      questionBn = "বাংলাদেশ টেস্ট ক্রিকেট দলের প্রথম অধিনায়ক কে ছিলেন?",
      optionsCsv = "নাঈমুর রহমান দুর্জয়||হাবিবুল বাশার||আমিনুল ইসলাম বুলবুল||খালেদ মাহমুদ",
      correctOptionIndex = 0,
      explanationBn = "২০০০ সালের নভেম্বর মাসে ভারতের বিপক্ষে অভিষেক টেস্টে বাংলাদেশ দলের নেতৃত্ব দেন নাঈমুর রহমান দুর্জয়।",
      categoryId = 12,
      levelNumber = 2,
      difficulty = "MEDIUM",
      points = 10
    ),
    // আন্তর্জাতিক
    QuestionEntity(
      id = 21,
      questionBn = "জাতিসংঘের বর্তমান মহাসচিব আন্তোনিও গুতেরেস কোন দেশের নাগরিক?",
      optionsCsv = "পর্তুগাল||স্পেন||ব্রাজিল||ইতালি",
      correctOptionIndex = 0,
      explanationBn = "আন্তোনিও গুতেরেস পর্তুগালের প্রাক্তন প্রধানমন্ত্রী এবং তিনি জাতিসংঘের ৯ম মহাসচিব।",
      categoryId = 13,
      levelNumber = 3,
      difficulty = "MEDIUM",
      points = 10
    ),
    // BCS
    QuestionEntity(
      id = 22,
      questionBn = "বাঙালি জাতির মুক্তির সনদ '৬ দফা দাবি' কত সালে পেশ করা হয়েছিল?",
      optionsCsv = "১৯৬৬ সালে||১৯৫২ সালে||১৯৫৪ সালে||১৯৬৯ সালে",
      correctOptionIndex = 0,
      explanationBn = "১৯৬৬ সালের ৫-৬ ফেব্রুয়ারি লাহোরে অনুষ্ঠিত বিরোধী দলগুলোর সম্মেলনে বঙ্গবন্ধু শেখ মুজিবুর রহমান ঐতিহাসিক ৬ দফা দাবি পেশ করেন।",
      categoryId = 15,
      levelNumber = 4,
      difficulty = "MEDIUM",
      points = 15
    ),
    QuestionEntity(
      id = 23,
      questionBn = "বাংলাদেশের সংবিধানে কতটি অনুচ্ছেদ রয়েছে?",
      optionsCsv = "১৫৩টি||১৪২টি||১৫০টি||১৬০টি",
      correctOptionIndex = 0,
      explanationBn = "বাংলাদেশের সংবিধানে মোট ১১টি ভাগ, ১৫৩টি অনুচ্ছেদ এবং ৭টি তফসিল রয়েছে।",
      categoryId = 15,
      levelNumber = 4,
      difficulty = "MEDIUM",
      points = 15
    ),
    // প্রাথমিক শিক্ষক ও চাকরি
    QuestionEntity(
      id = 24,
      questionBn = "কোনটি শুদ্ধ বানান?",
      optionsCsv = "মুমূর্ষু||মুমুর্ষু||মূমুষু||মুমূর্ষ",
      correctOptionIndex = 0,
      explanationBn = "সঠিক বানান হলো 'মুমূর্ষু' (হ্রস্ব-উ কার, দীর্ঘ-ঊ কার, রেফ সহ হ্রস্ব-উ কার)।",
      categoryId = 14,
      levelNumber = 2,
      difficulty = "MEDIUM",
      points = 10
    )
  )

  val initialExams: List<ExamEntity> = listOf(
    ExamEntity(
      id = 1,
      titleBn = "৪৬তম বিসিএস প্রিলিমিনারি স্পেশাল মডেল টেস্ট",
      descriptionBn = "বাংলাদেশ বিষয়াবলী, আন্তর্জাতিক, বাংলা ভাষা ও সাহিত্য এবং বিজ্ঞান থেকে পূর্ণাঙ্গ মডেল টেস্ট। প্রতিটি ভুল উত্তরের জন্য ০.২৫ নম্বর কাটা যাবে।",
      categoryId = 15,
      totalQuestions = 15,
      durationMinutes = 15,
      passingScore = 10,
      negativeMarkingPerWrong = 0.25f,
      maxAttempts = 3,
      isPublished = true
    ),
    ExamEntity(
      id = 2,
      titleBn = "বাংলাদেশ ব্যাংক এডি ও সিনিয়র অফিসার স্পেশাল",
      descriptionBn = "ব্যাংকিং খাতের জন্য সাধারণ জ্ঞান, সাম্প্রতিক তথ্য ও আইসিটি বিষয়ক বিশেষ পরীক্ষা। নেগেটিভ মার্কিং প্রযোজ্য।",
      categoryId = 16,
      totalQuestions = 12,
      durationMinutes = 12,
      passingScore = 8,
      negativeMarkingPerWrong = 0.25f,
      maxAttempts = 3,
      isPublished = true
    ),
    ExamEntity(
      id = 3,
      titleBn = "মুক্তিযুদ্ধ ও বাংলাদেশের ইতিহাস মেগা এক্সাম",
      descriptionBn = "১৯৭১ সালের মুক্তিযুদ্ধ, ভাষা আন্দোলন ও গৌরবময় ইতিহাসের উপর মেধা যাচাই পরীক্ষা।",
      categoryId = 2,
      totalQuestions = 10,
      durationMinutes = 10,
      passingScore = 7,
      negativeMarkingPerWrong = 0.25f,
      maxAttempts = 5,
      isPublished = true
    )
  )

  val initialNotifications: List<NotificationEntity> = listOf(
    NotificationEntity(
      id = 1,
      titleBn = "স্বাগতম বাংলা কুইজ মাস্টারে!",
      messageBn = "প্রতিদিন কুইজ খেলুন, পয়েন্ট অর্জন করুন এবং লিডারবোর্ডের শীর্ষে পৌঁছে জিতে নিন আকর্ষণীয় ব্যাজ ও রিওয়ার্ড।",
      type = "ANNOUNCEMENT"
    ),
    NotificationEntity(
      id = 2,
      titleBn = "আজকের দৈনিক কুইজ প্রকাশিত হয়েছে!",
      messageBn = "১০টি গুরুত্বপূর্ণ প্রশ্নের সঠিক উত্তর দিয়ে জিতে নিন বোনাস ৫০ পয়েন্ট। এখনই অংশ নিন!",
      type = "DAILY_QUIZ"
    ),
    NotificationEntity(
      id = 3,
      titleBn = "নতুন বিসিএস মডেল টেস্ট যুক্ত করা হয়েছে",
      messageBn = "আপনার বিসিএস ও সরকারি চাকরি প্রস্তুতি যাচাই করতে অংশ নিন নতুন মডেল টেস্টে।",
      type = "NEW_EXAM"
    )
  )

  val initialAdSettings = AdSettingsEntity()
  val initialAppSettings = AppSettingsEntity()
}
