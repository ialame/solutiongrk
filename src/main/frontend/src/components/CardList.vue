<script setup lang="ts">
interface Language {
  code: string;
}

interface CardTranslation {
  language: Language;
  name: string;
  description: string;
}

interface Card {
  id: number;
  cardNumber: string;
  gameType: string;
  imagePath: string;
  rarity: string | null;
  attack: number | null;
  defense: number | null;
  translations: CardTranslation[];
}

defineProps<{
  cards: Card[];
  loading: boolean;
}>();
</script>

<template>
  <div>
    <div v-if="loading">Chargement...</div>
    <div v-else-if="cards.length === 0">Aucune carte trouvée</div>
    <div v-else>
      <ul>
        <li v-for="card in cards" :key="card.id">
          <img :src="card.imagePath" alt="Card Image" style="max-width: 100px;" v-if="card.imagePath" />
          {{ card.translations.find(t => t.language.code === 'en')?.name || 'Nom non défini' }}
          (ATK: {{ card.attack ?? 'N/A' }}, DEF: {{ card.defense ?? 'N/A' }})
        </li>
      </ul>
    </div>
  </div>
</template>

<style scoped>
ul {
  list-style-type: none;
  padding: 0;
}
li {
  margin: 10px 0;
  padding: 10px;
  background-color: #f9f9f9;
  border-radius: 5px;
  display: flex;
  align-items: center;
}
img {
  margin-right: 10px;
}
</style>
