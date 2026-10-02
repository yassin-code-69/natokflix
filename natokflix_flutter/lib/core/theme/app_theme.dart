import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'app_colors.dart';

class AppTheme {
  static ThemeData darkTheme = ThemeData(
    useMaterial3: true,
    brightness: Brightness.dark,
    fontFamily: GoogleFonts.outfit().fontFamily,
    scaffoldBackgroundColor: AppColors.backgroundDark,
    colorScheme: const ColorScheme.dark(
      primary: AppColors.natokRed,
      onPrimary: Colors.white,
      primaryContainer: AppColors.natokRedDark,
      onPrimaryContainer: Colors.white,
      secondary: AppColors.natokGold,
      onSecondary: Colors.black,
      secondaryContainer: Color(0xFF3B2F00),
      onSecondaryContainer: AppColors.natokGoldLight,
      tertiary: AppColors.bkashPink,
      onTertiary: Colors.white,
      surface: AppColors.surfaceDark,
      onSurface: AppColors.textPrimary,
      outline: AppColors.borderDark,
    ),
    textTheme: const TextTheme(
      bodyLarge: TextStyle(
        color: AppColors.textPrimary,
        fontSize: 16,
        letterSpacing: 0.5,
      ),
      bodyMedium: TextStyle(
        color: AppColors.textSecondary,
        fontSize: 14,
      ),
    ),
    appBarTheme: const AppBarTheme(
      backgroundColor: AppColors.surfaceDark,
      elevation: 0,
      centerTitle: false,
      iconTheme: IconThemeData(color: Colors.white),
      titleTextStyle: TextStyle(
        color: Colors.white,
        fontSize: 20,
        fontWeight: FontWeight.bold,
      ),
    ),
    bottomNavigationBarTheme: const BottomNavigationBarThemeData(
      backgroundColor: AppColors.surfaceDark,
      selectedItemColor: AppColors.natokRed,
      unselectedItemColor: Color(0xFF888899),
      type: BottomNavigationBarType.fixed,
      elevation: 8,
      selectedLabelStyle: TextStyle(fontSize: 10, fontWeight: FontWeight.bold),
      unselectedLabelStyle: TextStyle(fontSize: 9),
    ),
    cardTheme: CardThemeData(
      color: AppColors.surfaceCard,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(12),
        side: const BorderSide(color: AppColors.borderDark, width: 1),
      ),
      elevation: 4,
    ),
    dialogTheme: DialogThemeData(
      backgroundColor: AppColors.surfaceDark,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(16),
        side: const BorderSide(color: AppColors.borderDark, width: 1),
      ),
    ),
  );

  static ThemeData lightTheme = ThemeData(
    useMaterial3: true,
    brightness: Brightness.light,
    fontFamily: GoogleFonts.outfit().fontFamily,
    scaffoldBackgroundColor: AppColors.backgroundLight,
    colorScheme: const ColorScheme.light(
      primary: AppColors.natokRed,
      onPrimary: Colors.white,
      primaryContainer: Color(0xFFFFDADA),
      onPrimaryContainer: AppColors.natokRedDark,
      secondary: AppColors.natokGold,
      onSecondary: Colors.black,
      surface: AppColors.surfaceLight,
      onSurface: AppColors.textPrimaryLight,
      outline: AppColors.borderLight,
    ),
  );
}
