/// Sampraday / Tradition options for Jain calendar
enum JainSampraday {
  tapagaccha,
  kharatargaccha,
  sthanakvasi,
  terapanth,
  digambar,
  custom,
}

/// Lunar Paksha (Fortnight)
enum Paksha {
  shukla, // Bright half (Sud)
  krishna, // Dark half (Vad)
}

/// Tithi names (1 to 15 in each Paksha)
class TithiNames {
  static const List<String> english = [
    'Pratipada (Ekad / 1)',
    'Dwitiya (Beej / 2)',
    'Tritiya (Trij / 3)',
    'Chaturthi (Chauth / 4)',
    'Panchami (Paancham / 5)',
    'Shashthi (Chhath / 6)',
    'Saptami (Saatam / 7)',
    'Ashtami (Aatham / 8)',
    'Navami (Naumam / 9)',
    'Dashami (Dasam / 10)',
    'Ekadashi (Agyaras / 11)',
    'Dwadashi (Baras / 12)',
    'Trayodashi (Teras / 13)',
    'Chaturdashi (Chaudas / 14)',
    'Purnima / Amavasya (15)',
  ];

  static const List<String> hindi = [
    'प्रतिपदा (एकम)',
    'द्वितीया (बीज)',
    'तृतीया (तीज)',
    'चतुर्थी (चौथ)',
    'पंचमी (पांचम)',
    'षष्ठी (छठ)',
    'सप्तमी (सातम)',
    'अष्टमी (आठम)',
    'नवमी (नौमी)',
    'दशमी (दसम)',
    'एकादशी (ग्यारस)',
    'द्वादशी (बारस)',
    'त्रयोदशी (तेरस)',
    'चतुर्दशी (चौदस)',
    'पूर्णिमा / अमावस्या',
  ];

  static const List<String> gujarati = [
    'પડવો / એકમ',
    'બીજ',
    'ત્રીજ',
    'ચોથ',
    'પાંચમ',
    'છઠ',
    'સાતમ',
    'આઠમ',
    'નોમ',
    'દસમ',
    'અગિયારસ',
    'બારસ',
    'તેરસ',
    'ચૌદસ',
    'પૂનમ / અમાસ',
  ];

  static String getName(int tithiNumber, Paksha paksha, {String lang = 'en'}) {
    final index = (tithiNumber - 1).clamp(0, 14);
    if (index == 14) {
      if (paksha == Paksha.shukla) {
        if (lang == 'gu') return 'પૂનમ';
        if (lang == 'hi') return 'पूर्णिमा';
        return 'Purnima';
      } else {
        if (lang == 'gu') return 'અમાસ';
        if (lang == 'hi') return 'अमावस्या';
        return 'Amavasya';
      }
    }
    if (lang == 'gu') return gujarati[index];
    if (lang == 'hi') return hindi[index];
    return english[index];
  }
}

/// The 27 Nakshatras
class NakshatraNames {
  static const List<String> english = [
    'Ashwini', 'Bharani', 'Krittika', 'Rohini', 'Mrigashirsha', 'Ardra',
    'Punarvasu', 'Pushya', 'Ashlesha', 'Magha', 'Purva Phalguni', 'Uttara Phalguni',
    'Hasta', 'Chitra', 'Swati', 'Vishakha', 'Anuradha', 'Jyeshtha',
    'Mula', 'Purva Ashadha', 'Uttara Ashadha', 'Shravana', 'Dhanishta',
    'Shatabhisha', 'Purva Bhadrapada', 'Uttara Bhadrapada', 'Revati'
  ];

  static const List<String> gujarati = [
    'અશ્વિની', 'ભરણી', 'કૃતિકા', 'રોહિણી', 'મૃગશીર્ષ', 'આર્દ્રા',
    'પુનર્વસુ', 'પુષ્ય', 'આશ્લેષા', 'મઘા', 'પૂર્વા ફાલ્ગુની', 'ઉત્તરા ફાલ્ગુની',
    'હસ્ત', 'ચિત્રા', 'સ્વાતી', 'વિશાખા', 'અનુરાધા', 'જ્યેષ્ઠા',
    'મૂળ', 'પૂર્વાષાઢા', 'ઉત્તરાષાઢા', 'શ્રવણ', 'ધનિષ્ઠા',
    'શતભિષા', 'પૂર્વા ભાદ્રપદ', 'ઉત્તરા ભાદ્રપદ', 'રેવતી'
  ];

  static const List<String> hindi = [
    'अश्विनी', 'भरणी', 'कृत्तिका', 'रोहिणी', 'मृगशीर्ष', 'आर्द्रा',
    'पुनर्वसु', 'पुष्य', 'आश्लेषा', 'मघा', 'पूर्वा फाल्गुनी', 'उत्तरा फाल्गुनी',
    'हस्त', 'चित्रा', 'स्वाति', 'विशाखा', 'अनुराधा', 'ज्येष्ठा',
    'मूल', 'पूर्वाषाढ़ा', 'उत्तराषाढ़ा', 'श्रवण', 'धनिष्ठा',
    'शतभिषा', 'पूर्वा भाद्रपद', 'उत्तरा भाद्रपद', 'रेवती'
  ];
}

/// The 27 Yogas
class YogaNames {
  static const List<String> english = [
    'Vishkambha', 'Priti', 'Ayushman', 'Saubhagya', 'Shobhana', 'Atiganda',
    'Sukarma', 'Dhriti', 'Shula', 'Ganda', 'Vriddhi', 'Dhruva',
    'Vyaghata', 'Harshana', 'Vajra', 'Siddhi', 'Vyatipata', 'Variyan',
    'Parigha', 'Shiva', 'Siddha', 'Sadhya', 'Shubha', 'Shukla',
    'Brahma', 'Indra', 'Vaidhriti'
  ];
}

/// The 11 Karanas (7 repeating + 4 fixed)
class KaranaNames {
  static const List<String> english = [
    'Bava', 'Balava', 'Kaulava', 'Taitila', 'Gara', 'Vanija', 'Vishti (Bhadra)',
    'Shakuni', 'Chatushpada', 'Naga', 'Kintughna'
  ];
}

/// 7 Varas (Days of week)
class VaraNames {
  static const List<String> english = [
    'Ravivara (Sunday)', 'Somavara (Monday)', 'Mangalavara (Tuesday)',
    'Budhavara (Wednesday)', 'Guruvara (Thursday)', 'Shukravara (Friday)', 'Shanivara (Saturday)'
  ];
  static const List<String> gujarati = [
    'રવિવાર', 'સોમવાર', 'મંગળવાર', 'બુધવાર', 'ગુરુવાર', 'શુક્રવાર', 'શનિવાર'
  ];
  static const List<String> hindi = [
    'रविवार', 'सोमवार', 'मंगलवार', 'बुधवार', 'गुरुवार', 'शुक्रवार', 'शनिवार'
  ];
}

/// Jain Months (Gujarati / Vikram Samvat & Veer Nirvan Samvat cycle)
/// Month starts with Kartak (Kartika) after Diwali for Gujarati/Jain calendar
class JainMonthNames {
  static const List<String> gujarati = [
    'કારતક', 'માગશર', 'પોષ', 'મહા', 'ફાગણ', 'ચૈત્ર',
    'વૈશાખ', 'જેઠ', 'અષાઢ', 'શ્રાવણ', 'ભાદરવો', 'આસો'
  ];

  static const List<String> hindi = [
    'कार्तिक', 'मार्गशीर्ष', 'पौष', 'माघ', 'फाल्गुन', 'चैत्र',
    'वैशाख', 'ज्येष्ठ', 'आषाढ़', 'श्रावण', 'भाद्रपद', 'आश्विन'
  ];

  static const List<String> english = [
    'Kartik', 'Margashirsha (Magshar)', 'Paush', 'Magh (Maha)', 'Phalgun', 'Chaitra',
    'Vaishakh', 'Jyeshtha (Jeth)', 'Ashadh', 'Shravan', 'Bhadrapad (Bhadarvo)', 'Ashvin (Aso)'
  ];
}
