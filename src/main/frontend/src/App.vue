<script setup lang="ts">
import { ref } from 'vue';
import CardForm from './components/CardForm.vue';
import CardList from './components/CardList.vue';

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

const cards = ref<Card[]>([]);
const loading = ref<boolean>(false);
const gameType = ref<string>('Yugioh');

const fetchCards = async () => {
  console.log('Début de fetchCards');
  loading.value = true;
  try {
    console.log('Envoi de la requête à /api/yugioh-cards');
    const response = await fetch(`/api/${gameType.value.toLowerCase()}-cards`);
    console.log('Réponse reçue :', response.status, response.statusText);
    if (!response.ok) {
      throw new Error(`Erreur HTTP : ${response.status} - ${response.statusText}`);
    }
    const data = await response.json();
    console.log('Données JSON brutes :', data);
    cards.value = data as Card[];
    console.log('Cards mis à jour :', cards.value);
  } catch (error) {
    console.error('Erreur lors du chargement des cartes :', error);
  } finally {
    loading.value = false;
    console.log('Fin de fetchCards, loading =', loading.value, 'cards =', cards.value);
  }
};

const handleDownload = async (selectedGameType: string, seriesCode: string) => {
  gameType.value = selectedGameType;
  loading.value = true;
  try {
    const response = await fetch(`/api/${gameType.value.toLowerCase()}-sets/process/${seriesCode}`, {
      method: 'POST'
    });
    if (!response.ok) {
      throw new Error(`Erreur HTTP : ${response.status} - ${response.statusText}`);
    }
    console.log('Série téléchargée avec succès');
    await fetchCards();
  } catch (error) {
    console.error('Erreur lors du téléchargement de la série :', error);
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div>
    <h1>Card Collection</h1>
    <CardForm @download="handleDownload" />
    <CardList :cards="cards" :loading="loading" />
  </div>
</template>

<style>
h1 {
  font-family: Arial, sans-serif;
  color: #2c3e50;
}
</style>
