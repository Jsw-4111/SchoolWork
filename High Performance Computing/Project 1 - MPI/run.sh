#!/usr/bin/env bash

#SBATCH --job-name=job.%J
#SBATCH --error=job.%J.err
#SBATCH --output=job.%J.out
#SBATCH --ntasks=8
#SBATCH --time=5:00
#SBATCH --mem-per-cpu=100

module load gcc
module load openmpi

cd ~/
mpirun ./ParallelSort