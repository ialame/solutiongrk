<script setup lang="ts">
import { ref, watch } from 'vue';

interface Language {
  code: string;
}

interface SetTranslationDTO {
  name: string;
  language: Language;
}

interface SetDTO {
  setCode: string;
  gameType: string;
  translations: SetTranslationDTO[];
  serieId: number;
}

const gameType = ref<string>('Yugioh');
const seriesCode = ref<string>('');
const availableSets = ref<SetDTO[]>([]);

const fetchSets = async () => {
  try {
    const response = await fetch(`/api/${gameType.value.toLowerCase()}-sets`);
    if (!response.ok) {
      throw new Error(`Erreur HTTP : ${response.status}`);
    }
    const data = await response.json();
    availableSets.value = data as SetDTO[];
    seriesCode.value = availableSets.value[0]?.setCode || '';
  } catch (error) {
    console.error('Erreur lors de la récupération des sets :', error);
    availableSets.value = [];
    seriesCode.value = '';
  }
};

watch(gameType, () => {
  fetchSets();
}, { immediate: true });

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
      <option v-for="set in availableSets" :key="set.setCode" :value="set.setCode">
        {{ set.translations.find(t => t.language.code === 'en')?.name || 'Nom non défini' }}
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
