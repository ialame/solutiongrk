<script setup lang="ts">
import { ref, onMounted } from 'vue';

// Interface correspondant à la structure JSON renvoyée par l'API
interface YugiohCard {
  id: number;
  cardNumber: string;
  gameType: string;
  imagePath: string;
  rarity: string | null;
  attack: number | null;
  defense: number | null;
  name?: string; // Optionnel car il pourrait venir des traductions
  translations?: { language: { code: string }, name: string, description: string }[];
}

const cards = ref<YugiohCard[]>([]);
const loading = ref<boolean>(false);

const fetchCards = async () => {
  console.log('Début de fetchCards');
  loading.value = true;
  try {
    console.log('Envoi de la requête à /api/yugioh-cards');
    const response = await fetch('/api/yugioh-cards', {
      method: 'GET',
      headers: {
        'Accept': 'application/json'
      }
    });
    console.log('Réponse reçue :', response.status, response.statusText);
    if (!response.ok) {
      throw new Error(`Erreur HTTP : ${response.status} - ${response.statusText}`);
    }
    const data = await response.json();
    console.log('Données JSON brutes :', data);
    cards.value = data as YugiohCard[];
    console.log('Cards mis à jour :', cards.value);
  } catch (error) {
    console.error('Erreur lors du chargement des cartes :', error);
  } finally {
    loading.value = false;
    console.log('Fin de fetchCards, loading =', loading.value, 'cards =', cards.value);
  }
};

onMounted(() => {
  console.log('Montage du composant, appel de fetchCards');
  fetchCards();
});
</script>

<template>
  <div>
    <h1>Yu-Gi-Oh Cards</h1>
    <div v-if="loading">Chargement...</div>
    <div v-else-if="cards.length === 0">Aucune carte trouvée</div>
    <div v-else>
      <ul>
        <li v-for="card in cards" :key="card.id">
          {{ card.name || 'Nom non défini' }} (ATK: {{ card.attack ?? 'N/A' }}, DEF: {{ card.defense ?? 'N/A' }})
        </li>
      </ul>
    </div>
  </div>
</template>

<style>
h1 {
  font-family: Arial, sans-serif;
  color: #2c3e50;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  margin: 10px 0;
  padding: 10px;
  background-color: #f9f9f9;
  border-radius: 5px;
}
</style>
