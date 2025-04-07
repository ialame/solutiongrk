<script setup lang="ts">
import { ref, watch } from 'vue';

// Liste des sets pour Yu-Gi-Oh et Pokémon
const yugiohSets = [
  { code: 'LOB', name: 'Legend of Blue Eyes White Dragon' },
  { code: 'MRD', name: 'Metal Raiders' },
  { code: 'SRL', name: 'Spell Ruler' },
  // Ajoutez d'autres sets Yu-Gi-Oh selon vos besoins
];

const pokemonSets = [
  { code: 'base1', name: 'Base Set' },
  { code: 'base2', name: 'Jungle' },
  { code: 'base3', name: 'Fossil' },
  // Ajoutez d'autres sets Pokémon selon vos besoins
];

const gameType = ref<string>('Yugioh');
const seriesCode = ref<string>('');

// Sélectionner le premier set par défaut pour activer le bouton
const availableSets = ref(yugiohSets);
seriesCode.value = availableSets.value[0]?.code || '';

// Mettre à jour les sets disponibles quand gameType change
watch(gameType, (newGameType) => {
  availableSets.value = newGameType === 'Yugioh' ? yugiohSets : pokemonSets;
  seriesCode.value = availableSets.value[0]?.code || ''; // Sélectionner le premier set
});

const emit = defineEmits<{
  (e: 'download', gameType: string, seriesCode: string): void;
}>();

const handleDownload = () => {
  if (!seriesCode.value) {
    alert('Veuillez sélectionner une série !');
    return;
  }
  emit('download', gameType.value, seriesCode.value);
};
</script>

<template>
  <div class="form-container">
    <label for="gameType">Type de jeu :</label>
    <select id="gameType" v-model="gameType">
      <option value="Yugioh">Yu-Gi-Oh</option>
      <option value="Pokemon">Pokémon</option>
    </select>
    <label for="seriesCode">Série :</label>
    <select id="seriesCode" v-model="seriesCode">
      <option v-for="set in availableSets" :key="set.code" :value="set.code">
        {{ set.name }}
      </option>
    </select>
    <button @click="handleDownload" :disabled="!seriesCode">
      Télécharger la série
    </button>
  </div>
</template>

<style scoped>
.form-container {
  margin-bottom: 20px;
}
.form-container label {
  margin-right: 10px;
}
.form-container select, .form-container input {
  padding: 5px;
  margin-right: 10px;
}
.form-container button {
  padding: 5px 10px;
  background-color: #2c3e50;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
.form-container button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}
</style>
