package org.telegram.messenger;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class HyinaManager {

    private static final SharedPreferences prefs = ApplicationLoader.applicationContext
            .getSharedPreferences("hyinagram_config", Activity.MODE_PRIVATE);

    // Активные флаги мода (по умолчанию всё включено на максимум)
    public static boolean ghostRead = prefs.getBoolean("ghostRead", true);
    public static boolean ghostTyping = prefs.getBoolean("ghostTyping", true);
    public static boolean ghostOnline = prefs.getBoolean("ghostOnline", false);
    public static boolean antiDelete = prefs.getBoolean("antiDelete", true);
    public static boolean bypassNoForwards = prefs.getBoolean("bypassNoForwards", true);
    public static boolean allowScreenshots = prefs.getBoolean("allowScreenshots", true);
    public static boolean disableAds = prefs.getBoolean("disableAds", true);
    public static boolean localPremium = prefs.getBoolean("localPremium", true);

    public static void toggle(String key) {
        boolean current = prefs.getBoolean(key, true);
        prefs.edit().putBoolean(key, !current).apply();
        reload();
    }

    public static void reload() {
        ghostRead = prefs.getBoolean("ghostRead", true);
        ghostTyping = prefs.getBoolean("ghostTyping", true);
        ghostOnline = prefs.getBoolean("ghostOnline", false);
        antiDelete = prefs.getBoolean("antiDelete", true);
        bypassNoForwards = prefs.getBoolean("bypassNoForwards", true);
        allowScreenshots = prefs.getBoolean("allowScreenshots", true);
        disableAds = prefs.getBoolean("disableAds", true);
        localPremium = prefs.getBoolean("localPremium", true);
    }

    // =========================================================================
    // ВСТРОЕННЫЙ ЭКРАН НАСТРОЕК HYINAGRAM (Открывается прямо внутри Телеграма)
    // =========================================================================
    public static class SettingsActivity extends BaseFragment {

        private RecyclerListView listView;
        private int rowCount = 0;
        private int headerGhostRow;
        private int ghostReadRow;
        private int ghostTypingRow;
        private int ghostOnlineRow;
        private int headerAyuRow;
        private int antiDeleteRow;
        private int bypassForwardsRow;
        private int allowScreenshotsRow;
        private int disableAdsRow;
        private int localPremiumRow;
        private int footerInfoRow;

        @Override
        public boolean onFragmentCreate() {
            super.onFragmentCreate();
            rowCount = 0;
            headerGhostRow = rowCount++;
            ghostReadRow = rowCount++;
            ghostTypingRow = rowCount++;
            ghostOnlineRow = rowCount++;
            headerAyuRow = rowCount++;
            antiDeleteRow = rowCount++;
            bypassForwardsRow = rowCount++;
            allowScreenshotsRow = rowCount++;
            disableAdsRow = rowCount++;
            localPremiumRow = rowCount++;
            footerInfoRow = rowCount++;
            return true;
        }

        @Override
        public View createView(Context context) {
            actionBar.setBackButtonImage(R.drawable.ic_ab_back);
            actionBar.setAllowOverlayTitle(true);
            actionBar.setTitle("Настройки Hyinagram 🚀");
            actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
                @Override
                public void onItemClick(int id) {
                    if (id == -1) finishFragment();
                }
            });

            fragmentView = new FrameLayout(context);
            FrameLayout frameLayout = (FrameLayout) fragmentView;
            frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));

            listView = new RecyclerListView(context);
            listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            listView.setVerticalScrollBarEnabled(false);
            frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

            listView.setAdapter(new RecyclerListView.SelectionAdapter() {
                @Override
                public int getItemCount() {
                    return rowCount;
                }

                @Override
                public boolean isEnabled(RecyclerView.ViewHolder holder) {
                    int type = holder.getItemViewType();
                    return type == 1;
                }

                @Override
                public int getItemViewType(int position) {
                    if (position == headerGhostRow || position == headerAyuRow) return 0;
                    if (position == footerInfoRow) return 2;
                    return 1;
                }

                @Override
                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view;
                    if (viewType == 0) {
                        view = new HeaderCell(context);
                        view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    } else if (viewType == 1) {
                        view = new TextCheckCell(context);
                        view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    } else {
                        view = new TextInfoPrivacyCell(context);
                    }
                    return new RecyclerListView.Holder(view);
                }

                @Override
                public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                    int type = holder.getItemViewType();
                    if (type == 0) {
                        HeaderCell cell = (HeaderCell) holder.itemView;
                        if (position == headerGhostRow) cell.setText("Режим Призрака (Stealth)");
                        else if (position == headerAyuRow) cell.setText("Модификации AyuGram & Extera");
                    } else if (type == 1) {
                        TextCheckCell cell = (TextCheckCell) holder.itemView;
                        if (position == ghostReadRow) {
                            cell.setTextAndCheck("Не отправлять «Прочитано»", ghostRead, true);
                        } else if (position == ghostTypingRow) {
                            cell.setTextAndCheck("Скрыть «Печатает...» и запись ГС", ghostTyping, true);
                        } else if (position == ghostOnlineRow) {
                            cell.setTextAndCheck("Вечный оффлайн", ghostOnline, true);
                        } else if (position == antiDeleteRow) {
                            cell.setTextAndCheck("Анти-удаление сообщений (🗑)", antiDelete, true);
                        } else if (position == bypassForwardsRow) {
                            cell.setTextAndCheck("Скачивание и пересылка из закрытых чатов", bypassNoForwards, true);
                        } else if (position == allowScreenshotsRow) {
                            cell.setTextAndCheck("Разрешить скриншоты везде", allowScreenshots, true);
                        } else if (position == disableAdsRow) {
                            cell.setTextAndCheck("Вырезать всю рекламу Telegram", disableAds, true);
                        } else if (position == localPremiumRow) {
                            cell.setTextAndCheck("Локальный Telegram Premium (иконки)", localPremium, false);
                        }
                    } else if (type == 2) {
                        TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                        cell.setText("Hyinagram v1.0.0 — Собрано специально без ограничений.");
                    }
                }
            });

            listView.setOnItemClickListener((view, position) -> {
                if (!(view instanceof TextCheckCell)) return;
                TextCheckCell cell = (TextCheckCell) view;
                if (position == ghostReadRow) {
                    toggle("ghostRead");
                    cell.setChecked(ghostRead);
                } else if (position == ghostTypingRow) {
                    toggle("ghostTyping");
                    cell.setChecked(ghostTyping);
                } else if (position == ghostOnlineRow) {
                    toggle("ghostOnline");
                    cell.setChecked(ghostOnline);
                } else if (position == antiDeleteRow) {
                    toggle("antiDelete");
                    cell.setChecked(antiDelete);
                } else if (position == bypassForwardsRow) {
                    toggle("bypassNoForwards");
                    cell.setChecked(bypassNoForwards);
                } else if (position == allowScreenshotsRow) {
                    toggle("allowScreenshots");
                    cell.setChecked(allowScreenshots);
                } else if (position == disableAdsRow) {
                    toggle("disableAds");
                    cell.setChecked(disableAds);
                } else if (position == localPremiumRow) {
                    toggle("localPremium");
                    cell.setChecked(localPremium);
                }
            });

            return fragmentView;
        }
    }
}
