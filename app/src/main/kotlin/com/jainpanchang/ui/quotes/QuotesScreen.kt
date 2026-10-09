package com.jainpanchang.ui.quotes

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jainpanchang.R
import com.jainpanchang.data.repository.QuotesRepository
import com.jainpanchang.data.schema.QuoteFileEntry
import com.jainpanchang.ui.theme.*
import java.time.LocalDate

@Composable
fun QuotesScreen(
    quotesRepository: QuotesRepository,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val today = remember { LocalDate.now() }
    val dailyQuote = remember { quotesRepository.getDailyQuote(today) }
    val allQuotes = remember { quotesRepository.getQuotes() }
    val lang = LocalAppLanguage.current

    fun shareQuote(q: QuoteFileEntry) {
        val mainText = q.text.get(lang)
        val textToShare = """
            "$mainText"
            
            — ${q.source}
            (Jain Panchang)
        """.trimIndent()

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, textToShare)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Quote")
        context.startActivity(shareIntent)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.title_quotes),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = SaffronPrimary,
                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
            )
        }

        // Today's Hero Quote Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SaffronPrimary),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatQuote,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(36.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = dailyQuote.text.get(lang),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 32.sp
                    )

                    // Secondary translation if not in English
                    if (lang != AppLanguage.ENGLISH) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = dailyQuote.text.en,
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "— ${dailyQuote.source}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = GoldenSecondaryContainer
                        )

                        FilledTonalButton(
                            onClick = { shareQuote(dailyQuote) },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color.White,
                                contentColor = SaffronPrimary
                            )
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(stringResource(R.string.share))
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = stringResource(R.string.title_quotes),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = SaffronPrimary
            )
        }

        items(allQuotes) { quote ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = quote.text.get(lang),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "— ${quote.source}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = GoldenSecondary
                        )
                        IconButton(onClick = { shareQuote(quote) }) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = SaffronPrimary)
                        }
                    }
                }
            }
        }
    }
}
