import 'package:flutter/material.dart';
import '../core/theme/app_colors.dart';

class GlowingNatokFlixLogo extends StatefulWidget {
  final double size;
  final bool showText;
  final double fontSize;
  final bool enablePulse;

  const GlowingNatokFlixLogo({
    super.key,
    this.size = 80.0,
    this.showText = false,
    this.fontSize = 24.0,
    this.enablePulse = true,
  });

  @override
  State<GlowingNatokFlixLogo> createState() => _GlowingNatokFlixLogoState();
}

class _GlowingNatokFlixLogoState extends State<GlowingNatokFlixLogo>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _scaleAnimation;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 1200),
    );

    _scaleAnimation = Tween<double>(begin: 0.96, end: 1.04).animate(
      CurvedAnimation(parent: _controller, curve: Curves.easeInOut),
    );

    if (widget.enablePulse) {
      _controller.repeat(reverse: true);
    }
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final logoWidget = AnimatedBuilder(
      animation: _scaleAnimation,
      builder: (context, child) {
        final scale = widget.enablePulse ? _scaleAnimation.value : 1.0;
        return Transform.scale(
          scale: scale,
          child: child,
        );
      },
      child: Container(
        width: widget.size,
        height: widget.size,
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(widget.size * 0.28),
          gradient: const RadialGradient(
            colors: [
              Color(0xFF38080C),
              Color(0xFF1F0407),
              Color(0xFF100204),
            ],
            radius: 0.85,
          ),
          border: Border.all(
            width: 2.0,
            color: AppColors.natokGold,
          ),
          boxShadow: [
            BoxShadow(
              color: AppColors.natokRed.withOpacity(0.5),
              blurRadius: 16,
              spreadRadius: 2,
            ),
            BoxShadow(
              color: AppColors.natokGold.withOpacity(0.3),
              blurRadius: 10,
              spreadRadius: 1,
            ),
          ],
        ),
        child: ClipRRect(
          borderRadius: BorderRadius.circular(widget.size * 0.28),
          child: CustomPaint(
            size: Size(widget.size, widget.size),
            painter: FilmReelLogoPainter(size: widget.size),
            child: Center(
              child: Padding(
                padding: EdgeInsets.only(left: widget.size * 0.06),
                child: Icon(
                  Icons.play_arrow,
                  color: const Color(0xFFFFF6D6),
                  size: widget.size * 0.44,
                ),
              ),
            ),
          ),
        ),
      ),
    );

    if (!widget.showText) {
      return logoWidget;
    }

    return Row(
      mainAxisSize: MainAxisSize.min,
      crossAxisAlignment: CrossAxisAlignment.center,
      children: [
        logoWidget,
        const SizedBox(width: 10),
        Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text(
              'Natok',
              style: TextStyle(
                fontSize: widget.fontSize,
                fontWeight: FontWeight.w900,
                color: Colors.white,
                letterSpacing: -0.5,
              ),
            ),
            Text(
              'Flix',
              style: TextStyle(
                fontSize: widget.fontSize,
                fontWeight: FontWeight.w900,
                color: AppColors.natokRed,
                letterSpacing: -0.5,
              ),
            ),
          ],
        ),
      ],
    );
  }
}

class FilmReelLogoPainter extends CustomPainter {
  final double size;

  FilmReelLogoPainter({required this.size});

  @override
  void paint(Canvas canvas, Size canvasSize) {
    final w = canvasSize.width;
    final h = canvasSize.height;
    final notchRadius = w * 0.045;

    final notchPaint = Paint()
      ..color = const Color(0xFFFFD54F).withOpacity(0.6)
      ..style = PaintingStyle.fill;

    const notchCount = 5;
    final step = w / (notchCount + 1);

    for (int i = 1; i <= notchCount; i++) {
      final x = step * i;
      // Top notches
      canvas.drawCircle(Offset(x, h * 0.12), notchRadius, notchPaint);
      // Bottom notches
      canvas.drawCircle(Offset(x, h * 0.88), notchRadius, notchPaint);
    }

    // 3D Play Button Triangle with gradient depth
    final path = Path()
      ..moveTo(w * 0.38, h * 0.28)
      ..lineTo(w * 0.72, h * 0.50)
      ..lineTo(w * 0.38, h * 0.72)
      ..close();

    final playPaint = Paint()
      ..shader = const LinearGradient(
        colors: [
          AppColors.natokGoldLight,
          AppColors.natokGold,
          Color(0xFFCC8400),
        ],
      ).createShader(Rect.fromLTWH(0, 0, w, h))
      ..style = PaintingStyle.fill;

    canvas.drawPath(path, playPaint);
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}
