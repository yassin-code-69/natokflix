import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../core/theme/app_colors.dart';
import '../../components/glowing_logo.dart';
import '../../providers/natok_provider.dart';

class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> with SingleTickerProviderStateMixin {
  late TabController _tabController;

  // Login Controllers
  final _loginUserCtrl = TextEditingController(text: 'admin');
  final _loginPassCtrl = TextEditingController(text: 'admin123');
  bool _rememberMe = true;
  bool _loginPassObscure = true;

  // Register Controllers
  final _regNameCtrl = TextEditingController();
  final _regEmailCtrl = TextEditingController();
  final _regPhoneCtrl = TextEditingController();
  final _regPassCtrl = TextEditingController();
  final _regRefCtrl = TextEditingController();
  String _selectedGender = 'Boy';
  final String _selectedBirthday = '15-08-2002';
  bool _regPassObscure = true;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 2, vsync: this);
  }

  @override
  void dispose() {
    _tabController.dispose();
    _loginUserCtrl.dispose();
    _loginPassCtrl.dispose();
    _regNameCtrl.dispose();
    _regEmailCtrl.dispose();
    _regPhoneCtrl.dispose();
    _regPassCtrl.dispose();
    _regRefCtrl.dispose();
    super.dispose();
  }

  void _showForgotPasswordDialog() {
    final emailCtrl = TextEditingController();
    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        title: Row(
          children: const [
            Icon(Icons.lock_reset, color: AppColors.natokGold),
            SizedBox(width: 8),
            Text(
              'পাসওয়ার্ড রিসেট',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
          ],
        ),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              'আপনার অ্যাকাউন্টের ইমেইল অথবা মোবাইল নম্বর লিখুন। পাসওয়ার্ড রিসেট নির্দেশিকা পাঠানো হবে।',
              style: TextStyle(fontSize: 13, color: AppColors.textSecondary),
            ),
            const SizedBox(height: 16),
            TextField(
              controller: emailCtrl,
              decoration: InputDecoration(
                hintText: 'ইমেইল বা মোবাইল',
                filled: true,
                fillColor: AppColors.surfaceVariantDark,
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(10),
                  borderSide: const BorderSide(color: AppColors.borderDark),
                ),
                prefixIcon: const Icon(Icons.email, color: AppColors.textSecondary),
              ),
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(ctx),
            child: const Text('বাতিল', style: TextStyle(color: Colors.white70)),
          ),
          ElevatedButton(
            onPressed: () {
              Navigator.pop(ctx);
              ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(
                  content: Text('রিসেট নির্দেশিকা পাঠানো হয়েছে।'),
                  backgroundColor: AppColors.statusSuccess,
                ),
              );
            },
            style: ElevatedButton.styleFrom(backgroundColor: AppColors.natokRed),
            child: const Text('পাঠান', style: TextStyle(color: Colors.white)),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final provider = context.watch<NatokProvider>();

    return Scaffold(
      backgroundColor: AppColors.backgroundDark,
      body: SafeArea(
        child: Center(
          child: SingleChildScrollView(
            padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
            child: ConstrainedBox(
              constraints: const BoxConstraints(maxWidth: 480),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const SizedBox(height: 12),
                  const GlowingNatokFlixLogo(
                    size: 80,
                    showText: true,
                    fontSize: 26,
                  ),
                  const SizedBox(height: 8),
                  const Text(
                    'বাংলা নাটকের প্রিমিয়াম স্ট্রিমিং ও আর্নিং প্ল্যাটফর্ম',
                    textAlign: TextAlign.center,
                    style: TextStyle(
                      fontSize: 12,
                      color: AppColors.textSecondary,
                    ),
                  ),
                  const SizedBox(height: 24),

                  if (provider.loginErrorAlert != null) ...[
                    Container(
                      width: double.infinity,
                      padding: const EdgeInsets.all(12),
                      decoration: BoxDecoration(
                        color: AppColors.statusRejected.withOpacity(0.15),
                        borderRadius: BorderRadius.circular(10),
                        border: Border.all(color: AppColors.statusRejected),
                      ),
                      child: Text(
                        provider.loginErrorAlert!,
                        style: const TextStyle(
                          color: AppColors.statusRejected,
                          fontSize: 13,
                        ),
                      ),
                    ),
                    const SizedBox(height: 16),
                  ],

                  // Tab Bar
                  Container(
                    decoration: BoxDecoration(
                      color: AppColors.surfaceDark,
                      borderRadius: BorderRadius.circular(12),
                      border: Border.all(color: AppColors.borderDark),
                    ),
                    child: TabBar(
                      controller: _tabController,
                      indicatorColor: AppColors.natokRed,
                      indicatorWeight: 3,
                      labelColor: AppColors.natokRed,
                      unselectedLabelColor: AppColors.textSecondary,
                      labelStyle: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14),
                      tabs: const [
                        Tab(text: 'লগইন'),
                        Tab(text: 'রেজিস্ট্রেশন'),
                      ],
                    ),
                  ),
                  const SizedBox(height: 20),

                  // Tab Content
                  SizedBox(
                    height: 480,
                    child: TabBarView(
                      controller: _tabController,
                      children: [
                        _buildLoginForm(provider),
                        _buildRegisterForm(provider),
                      ],
                    ),
                  ),

                  const SizedBox(height: 16),
                  const Text(
                    'অ্যাডমিন ডেমো লগইন: admin / admin123',
                    style: TextStyle(
                      fontSize: 11,
                      color: AppColors.natokGold,
                      fontWeight: FontWeight.w500,
                    ),
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildLoginForm(NatokProvider provider) {
    return SingleChildScrollView(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          TextField(
            controller: _loginUserCtrl,
            decoration: InputDecoration(
              labelText: 'ব্যবহারকারীর নাম বা ইমেইল',
              filled: true,
              fillColor: AppColors.surfaceDark,
              prefixIcon: const Icon(Icons.person, color: AppColors.textSecondary),
              border: OutlineInputBorder(
                borderRadius: BorderRadius.circular(12),
                borderSide: const BorderSide(color: AppColors.borderDark),
              ),
            ),
          ),
          const SizedBox(height: 16),
          TextField(
            controller: _loginPassCtrl,
            obscureText: _loginPassObscure,
            decoration: InputDecoration(
              labelText: 'পাসওয়ার্ড',
              filled: true,
              fillColor: AppColors.surfaceDark,
              prefixIcon: const Icon(Icons.lock, color: AppColors.textSecondary),
              suffixIcon: IconButton(
                icon: Icon(
                  _loginPassObscure ? Icons.visibility : Icons.visibility_off,
                  color: AppColors.textSecondary,
                ),
                onPressed: () {
                  setState(() {
                    _loginPassObscure = !_loginPassObscure;
                  });
                },
              ),
              border: OutlineInputBorder(
                borderRadius: BorderRadius.circular(12),
                borderSide: const BorderSide(color: AppColors.borderDark),
              ),
            ),
          ),
          const SizedBox(height: 8),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Row(
                children: [
                  Checkbox(
                    value: _rememberMe,
                    activeColor: AppColors.natokRed,
                    onChanged: (val) {
                      setState(() {
                        _rememberMe = val ?? true;
                      });
                    },
                  ),
                  const Text(
                    'মনে রাখুন',
                    style: TextStyle(fontSize: 13, color: AppColors.textSecondary),
                  ),
                ],
              ),
              TextButton(
                onPressed: _showForgotPasswordDialog,
                child: const Text(
                  'পাসওয়ার্ড ভুলে গেছেন?',
                  style: TextStyle(
                    fontSize: 12,
                    color: AppColors.natokGold,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: 12),
          SizedBox(
            width: double.infinity,
            height: 48,
            child: ElevatedButton(
              onPressed: () {
                provider.handleLogin(
                  _loginUserCtrl.text.trim(),
                  _loginPassCtrl.text.trim(),
                  _rememberMe,
                );
              },
              style: ElevatedButton.styleFrom(
                backgroundColor: AppColors.natokRed,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(12),
                ),
              ),
              child: const Text(
                'লগইন করুন',
                style: TextStyle(fontSize: 15, fontWeight: FontWeight.bold, color: Colors.white),
              ),
            ),
          ),
          const SizedBox(height: 20),
          Row(
            children: const [
              Expanded(child: Divider(color: AppColors.borderDark)),
              Padding(
                padding: EdgeInsets.symmetric(horizontal: 12),
                child: Text(
                  'অথবা সরাসরি প্রবেশ করুন',
                  style: TextStyle(fontSize: 11, color: AppColors.textMuted),
                ),
              ),
              Expanded(child: Divider(color: AppColors.borderDark)),
            ],
          ),
          const SizedBox(height: 16),
          Row(
            children: [
              Expanded(
                child: OutlinedButton.icon(
                  onPressed: () => provider.handleGoogleSignIn(),
                  icon: const Icon(Icons.g_mobiledata, color: Colors.white, size: 24),
                  label: const Text('Google', style: TextStyle(color: Colors.white, fontSize: 13)),
                  style: OutlinedButton.styleFrom(
                    side: const BorderSide(color: AppColors.borderDark),
                    padding: const EdgeInsets.symmetric(vertical: 12),
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                  ),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: OutlinedButton.icon(
                  onPressed: () => provider.handleFacebookSignIn(),
                  icon: const Icon(Icons.facebook, color: Color(0xFF1877F2), size: 20),
                  label: const Text('Facebook', style: TextStyle(color: Colors.white, fontSize: 13)),
                  style: OutlinedButton.styleFrom(
                    side: const BorderSide(color: AppColors.borderDark),
                    padding: const EdgeInsets.symmetric(vertical: 12),
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                  ),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildRegisterForm(NatokProvider provider) {
    return SingleChildScrollView(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          TextField(
            controller: _regNameCtrl,
            decoration: InputDecoration(
              labelText: 'পূর্ণ নাম',
              filled: true,
              fillColor: AppColors.surfaceDark,
              prefixIcon: const Icon(Icons.person_outline, color: AppColors.textSecondary),
              border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
            ),
          ),
          const SizedBox(height: 12),
          TextField(
            controller: _regEmailCtrl,
            decoration: InputDecoration(
              labelText: 'ইমেইল অ্যাড্রেস',
              filled: true,
              fillColor: AppColors.surfaceDark,
              prefixIcon: const Icon(Icons.email_outlined, color: AppColors.textSecondary),
              border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
            ),
          ),
          const SizedBox(height: 12),
          TextField(
            controller: _regPhoneCtrl,
            decoration: InputDecoration(
              labelText: 'মোবাইল নম্বর (১১ ডিজিট)',
              filled: true,
              fillColor: AppColors.surfaceDark,
              prefixIcon: const Icon(Icons.phone_outlined, color: AppColors.textSecondary),
              border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
            ),
          ),
          const SizedBox(height: 12),
          TextField(
            controller: _regPassCtrl,
            obscureText: _regPassObscure,
            decoration: InputDecoration(
              labelText: 'পাসওয়ার্ড তৈরি করুন',
              filled: true,
              fillColor: AppColors.surfaceDark,
              prefixIcon: const Icon(Icons.lock_outline, color: AppColors.textSecondary),
              suffixIcon: IconButton(
                icon: Icon(
                  _regPassObscure ? Icons.visibility : Icons.visibility_off,
                  color: AppColors.textSecondary,
                ),
                onPressed: () {
                  setState(() {
                    _regPassObscure = !_regPassObscure;
                  });
                },
              ),
              border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
            ),
          ),
          const SizedBox(height: 12),
          // Gender Selector
          Row(
            children: [
              const Text(
                'জেন্ডার: ',
                style: TextStyle(color: AppColors.textSecondary, fontSize: 13),
              ),
              const SizedBox(width: 8),
              ChoiceChip(
                label: const Text('ছেলে 👦'),
                selected: _selectedGender == 'Boy',
                selectedColor: const Color(0xFF1E88E5).withOpacity(0.3),
                onSelected: (selected) {
                  if (selected) setState(() => _selectedGender = 'Boy');
                },
              ),
              const SizedBox(width: 8),
              ChoiceChip(
                label: const Text('মেয়ে 👧'),
                selected: _selectedGender == 'Girl',
                selectedColor: AppColors.bkashPink.withOpacity(0.3),
                onSelected: (selected) {
                  if (selected) setState(() => _selectedGender = 'Girl');
                },
              ),
            ],
          ),
          const SizedBox(height: 12),
          TextField(
            controller: _regRefCtrl,
            decoration: InputDecoration(
              labelText: 'রেফারেল কোড (ঐচ্ছিক - দিলে ১০০ পয়েন্ট ফ্রি)',
              filled: true,
              fillColor: AppColors.surfaceDark,
              prefixIcon: const Icon(Icons.card_giftcard, color: AppColors.natokGold),
              border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
            ),
          ),
          const SizedBox(height: 16),
          SizedBox(
            width: double.infinity,
            height: 48,
            child: ElevatedButton(
              onPressed: () {
                provider.handleSignUp(
                  _regNameCtrl.text.trim(),
                  _regEmailCtrl.text.trim(),
                  _regPhoneCtrl.text.trim(),
                  _regPassCtrl.text.trim(),
                  _regRefCtrl.text.trim(),
                  _selectedGender,
                  _selectedBirthday,
                );
              },
              style: ElevatedButton.styleFrom(
                backgroundColor: AppColors.natokRed,
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
              ),
              child: const Text(
                'রেজিস্ট্রেশন সম্পন্ন করুন',
                style: TextStyle(fontSize: 15, fontWeight: FontWeight.bold, color: Colors.white),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
