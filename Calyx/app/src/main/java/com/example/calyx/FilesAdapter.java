package com.example.calyx;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.ViewHolder> {

        private List<File> files = new ArrayList<>();
        private static final int DIRECTORY = 0;
        private static final int FILE = 1;

        @Nullable
        private OnFileClickListener onFileClickListener;


        //УСТАНОВКА СЛУШАТЕЛЯ ДЛЯ ПОЛУЧЕНИЯ КЛИКОВ ИЗ СПИСКА
        public void setOnFileClickListener(@Nullable OnFileClickListener onFileClickListener) {
            this.onFileClickListener = onFileClickListener;
        }

        //ПЕРЕНОС ДАННЫХ В ОБЪЕКТ КЛАССА
        public void setFiles(List<File> files) {
            this.files = files;
        }


        //УСТАНОВКА РАЗМЕТКИ
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view;
            if (viewType == FILE) {
                view = layoutInflater.inflate(R.layout.file_mng_item, parent, false);
            }
            else{
                view = layoutInflater.inflate(R.layout.dir_mng_item, parent, false);
            }
            return new ViewHolder(view);
        }

        //СВЯЗЬ ФАЙЛОВ С ЭЛЕМЕНТАМИ СПИСКА
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            File file = files.get(position);
            holder.nameTv.setText(file.getName());
            holder.itemView.setTag(file);
        }

        //ПОЛУЧЕНИЕ КОЛИЧЕСТВА ФАЙЛОВ/ДИРЕКТОРИЙ
        @Override
        public int getItemCount() {
            return files.size();
        }


        //ДЛЯ ТОГО, ЧТОБ РАЗЛИЧАТЬ ФАЙЛЫ И ДИРЕКТОРИИ
        @Override
        public int getItemViewType(int position) {
            File file = files.get(position);
            if (file.isDirectory()) {
                return DIRECTORY;
            } else {
                return FILE;
            }
        }


        //ДЛЯ ОБРАБОТКИ НАЖАТИЯ
        class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTv;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.name_tv);

            itemView.setOnClickListener(new View.OnClickListener() { //слушатель нажалий на item
                @Override
                public void onClick(View view) {
                    File file = (File) view.getTag(); // получение файла по тегу
                    if (onFileClickListener != null) {
                        //список отправляет в активити файл
                        onFileClickListener.onFileClick(file);
                    }
                }
            });
        }
        }


        //ПЕРЕДАЧИ КЛИКА ИЗ СПИСКА В АКТИВИТИ / А-ЛЯ СИГНАЛ
        public interface OnFileClickListener {
            void onFileClick(File file);
        }
}
