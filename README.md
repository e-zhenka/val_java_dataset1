Информация о датасете здесь: https://paperswithcode.com/dataset/vulnerability-java-dataset

он использовался для fine-tuning llm вот здесь https://arxiv.org/pdf/2401.17010v5

(на стр.7 написано, как он собирался и из чего состоит) скачать можно здесь: https://github.com/rmusab/vul-llm-finetune/tree/main/Datasets/without_p3

Датасет для файнтьюнинга LLM собран из трёх источников:

CVEfixes – автоматически собранные данные об уязвимостях и их исправлениях (возможны ошибки).

Manually-Curated Dataset – ручной сбор данных по уязвимостям в Java (более надежный).

VCMatch – содержит только fix-коммиты для 10 популярных репозиториев.

Процесс разметки: Выделение функций из коммитов, исправляющих уязвимости:

До изменения (pre-change) → уязвимые (Vulnerable, P1).

После изменения (post-change) → неуязвимые (Non-vulnerable, P2).

Неизмененные функции в файлах с патчами → неуязвимые (Easy negatives, P3).

Включены только коммиты, где изменена одна функция (датасет X1).

Итоговые датасеты:

X1 без P3: 1334(1:1 баланс уязвимых/неуязвимых). https://github.com/rmusab/vul-llm-finetune/blob/main/Datasets/without_p3/java_k_1_strict_2023_07_03.tar.gz

X1 с P3: 22945 (1:34 баланс, большинство – P3) https://github.com/rmusab/vul-llm-finetune/blob/main/Datasets/with_p3/java_k_1_strict_2023_06_30.tar.gz
