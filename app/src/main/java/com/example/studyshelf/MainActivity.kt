Text(
text = "My Subjects (${subjects.size})",
style = MaterialTheme.typography.titleMedium,
fontWeight = FontWeight.Bold
)

Spacer(modifier = Modifier.height(8.dp))

if (subjects.isEmpty()) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("No subjects yet. Tap '+ Add' to add your first college subject!")
    }
} else {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(subjects) { subject ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSubjectClick(subject) },
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFF2E7D32), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = subject.name.take(1).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = subject.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            if (subject.description.isNotBlank()) {
                                Text(
                                    text = subject.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.DarkGray
                                )
                            }
                        }
                    }
                    TextButton(onClick = { onEditSubjectClick(subject) }) {
                        Text("Edit", color = Color(0xFF2E7D32))
                    }
                }
            }
        }
    }
}
}
}
}